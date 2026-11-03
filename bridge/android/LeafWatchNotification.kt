package app.leafvibe.leafwatch.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Creates a real local Android notification for the LeafWatch smoke test.
 *
 * This is a phone notification. If Galaxy Wearable is configured to mirror
 * LeafWatch notifications, the Galaxy Fit3 may display it like other app alerts.
 * No Internet connection or BLE UUID is involved in this first test.
 */
object LeafWatchNotification {
    const val CHANNEL_ID = "leafwatch_test"
    const val NOTIFICATION_ID = 1001

    fun hasPostNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun showTest001(context: Context): Boolean {
        if (!hasPostNotificationPermission(context)) return false

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LeafWatch",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Messages de test locaux LeafWatch"
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("LeafWatch — TEST 001")
            .setContentText("Bonjour ! Le test de notification locale LeafWatch fonctionne.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Bonjour ! Ceci est le premier message de test LeafWatch. " +
                        "Objectif : vérifier la réception sur ta Galaxy Fit3, sans Internet. " +
                        "LeafVibe — Connexion locale."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
