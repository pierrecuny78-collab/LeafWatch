package app.leafvibe.leafwatch.bridge

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONObject

/**
 * Durable offline queue for LeafWatch envelopes.
 *
 * This class uses Android's private app database. It has no network dependency.
 * Do not enqueue private documents until encryption-at-rest and the BLE security
 * model have been implemented and reviewed.
 */
class OfflineOutbox(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Message(
        val messageId: String,
        val envelopeJson: String,
        val createdAt: String,
        val attempts: Int
    )

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                message_id TEXT PRIMARY KEY NOT NULL,
                envelope_json TEXT NOT NULL,
                created_at TEXT NOT NULL,
                status TEXT NOT NULL CHECK(status IN ('PENDING', 'DELIVERED')),
                attempts INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_leafwatch_outbox_status ON $TABLE(status, created_at)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Keep migrations explicit. Never silently discard queued user messages.
        if (oldVersion != newVersion) {
            throw IllegalStateException("Missing LeafWatch outbox migration: $oldVersion -> $newVersion")
        }
    }

    /** Persist a valid protocol envelope before attempting any Bluetooth transfer. */
    @Synchronized
    fun enqueue(envelopeJson: String): Boolean {
        require(envelopeJson.toByteArray(Charsets.UTF_8).size <= MAX_ENVELOPE_BYTES) {
            "LeafWatch envelope exceeds the 64 KiB limit"
        }

        val envelope = JSONObject(envelopeJson)
        require(envelope.optString("protocol") == PROTOCOL) { "Unsupported LeafWatch protocol" }
        val messageId = envelope.optString("message_id")
        val createdAt = envelope.optString("created_at")
        require(messageId.isNotBlank() && messageId.length <= 80) { "Invalid message_id" }
        require(createdAt.isNotBlank()) { "Missing created_at" }
        require(envelope.optJSONObject("payload") != null) { "payload must be an object" }

        val values = ContentValues().apply {
            put("message_id", messageId)
            put("envelope_json", envelope.toString())
            put("created_at", createdAt)
            put("status", STATUS_PENDING)
            put("attempts", 0)
        }
        val inserted = writableDatabase.insertWithOnConflict(
            TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE
        )
        // false means this ID was already queued; retries must not create duplicates.
        return inserted != -1L
    }

    @Synchronized
    fun pending(limit: Int = DEFAULT_BATCH_SIZE): List<Message> {
        require(limit in 1..MAX_BATCH_SIZE) { "Invalid batch size" }
        val result = mutableListOf<Message>()
        readableDatabase.query(
            TABLE,
            arrayOf("message_id", "envelope_json", "created_at", "attempts"),
            "status = ?",
            arrayOf(STATUS_PENDING),
            null,
            null,
            "created_at ASC",
            limit.toString()
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += Message(
                    messageId = cursor.getString(0),
                    envelopeJson = cursor.getString(1),
                    createdAt = cursor.getString(2),
                    attempts = cursor.getInt(3)
                )
            }
        }
        return result
    }

    /** Record a failed or interrupted attempt without deleting the message. */
    @Synchronized
    fun recordAttempt(messageId: String) {
        val values = ContentValues().apply {
            put("attempts", "attempts + 1")
        }
        writableDatabase.execSQL(
            "UPDATE $TABLE SET attempts = attempts + 1 WHERE message_id = ? AND status = ?",
            arrayOf(messageId, STATUS_PENDING)
        )
    }

    /**
     * Call only after receiving an acknowledgement from the watch whose message_id
     * exactly matches the queued message. A write attempt alone is not delivery.
     */
    @Synchronized
    fun markDelivered(acknowledgedMessageId: String): Boolean {
        val values = ContentValues().apply { put("status", STATUS_DELIVERED) }
        val changed = writableDatabase.update(
            TABLE,
            values,
            "message_id = ? AND status = ?",
            arrayOf(acknowledgedMessageId, STATUS_PENDING)
        )
        return changed == 1
    }

    @Synchronized
    fun pendingCount(): Int {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE WHERE status = ?",
            arrayOf(STATUS_PENDING)
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    companion object {
        private const val DATABASE_NAME = "leafwatch_outbox.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE = "outbox"
        private const val PROTOCOL = "leafwatch.transfer.v1"
        private const val STATUS_PENDING = "PENDING"
        private const val STATUS_DELIVERED = "DELIVERED"
        private const val MAX_ENVELOPE_BYTES = 64 * 1024
        private const val DEFAULT_BATCH_SIZE = 20
        private const val MAX_BATCH_SIZE = 100
    }
}
