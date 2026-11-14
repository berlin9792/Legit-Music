package legit.music.ayush.maintenance

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import legit.music.ayush.MainActivity
import legit.music.ayush.R
import timber.log.Timber

private const val NOTIFICATION_CHANNEL_ID = "service_updates"
private const val NOTIFICATION_ID = 7201

class MaintenanceMessagingService : FirebaseMessagingService() {
  override fun onMessageReceived(message: RemoteMessage) {
    Timber.tag("MaintenanceMessaging").d(
      "FCM message received; hasNotification=${message.notification != null}, dataKeys=${message.data.keys}"
    )

    if (message.data["action"] == "refresh_maintenance") {
      RemoteMaintenance.refreshFromPush()
    }

    showForegroundNotification(message)
  }

  private fun showForegroundNotification(message: RemoteMessage) {
    val title =
      message.notification?.title ?: message.data["title"] ?: return
    val body =
      message.notification?.body ?: message.data["body"]
        ?: getString(R.string.remote_maintenance_notification_text)
    val notificationManager = getSystemService(NotificationManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.createNotificationChannel(
        NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          getString(R.string.remote_maintenance_notification_channel),
          NotificationManager.IMPORTANCE_DEFAULT,
        )
      )
    }

    val launchIntent = Intent(this, MainActivity::class.java)
    val pendingIntent =
      PendingIntent.getActivity(
        this,
        NOTIFICATION_ID,
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
    val notification =
      NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.echomusicnotification)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(NOTIFICATION_ID, notification)
  }
}