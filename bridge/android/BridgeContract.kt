package app.leafvibe.leafwatch.bridge

/**
 * Boundary between the offline queue and a future verified Fit3 BLE transport.
 *
 * No UUIDs or GATT assumptions are encoded here. Implement this interface only
 * after the target Fit3 firmware's service, characteristics, framing and
 * acknowledgement behavior have been verified on physical hardware.
 */
interface LeafWatchTransport {
    fun isReady(): Boolean

    /**
     * Send one already-persisted envelope.
     * The callback must report acknowledgedMessageId only after a real receiver
     * acknowledgement has been validated. A successful GATT write is not enough.
     */
    fun send(
        envelopeJson: String,
        callback: (TransportResult) -> Unit
    )
}

sealed class TransportResult {
    data class Acknowledged(val acknowledgedMessageId: String) : TransportResult()
    data class RetryableFailure(val reason: String) : TransportResult()
    data class PermanentFailure(val reason: String) : TransportResult()
}

/**
 * Small orchestration helper. It never removes pending messages on a write attempt;
 * only a matching receiver acknowledgement marks an item delivered.
 */
class OfflineSyncCoordinator(
    private val outbox: OfflineOutbox,
    private val transport: LeafWatchTransport
) {
    fun syncPending(onFinished: (sent: Int, remaining: Int) -> Unit) {
        if (!transport.isReady()) {
            onFinished(0, outbox.pendingCount())
            return
        }

        val batch = outbox.pending()
        if (batch.isEmpty()) {
            onFinished(0, 0)
            return
        }

        var completed = 0
        var acknowledged = 0

        fun finishOne() {
            completed += 1
            if (completed == batch.size) {
                onFinished(acknowledged, outbox.pendingCount())
            }
        }

        batch.forEach { message ->
            transport.send(message.envelopeJson) { result ->
                when (result) {
                    is TransportResult.Acknowledged -> {
                        if (result.acknowledgedMessageId == message.messageId &&
                            outbox.markDelivered(result.acknowledgedMessageId)
                        ) {
                            acknowledged += 1
                        }
                    }
                    is TransportResult.RetryableFailure -> outbox.recordAttempt(message.messageId)
                    is TransportResult.PermanentFailure -> outbox.recordAttempt(message.messageId)
                }
                finishOne()
            }
        }
    }
}
