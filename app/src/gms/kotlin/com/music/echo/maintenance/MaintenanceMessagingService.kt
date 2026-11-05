package echo.music.iad1tya.maintenance

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MaintenanceMessagingService : FirebaseMessagingService() {
  override fun onMessageReceived(message: RemoteMessage) {
    if (message.data["action"] == "refresh_maintenance") {
      RemoteMaintenance.refreshFromPush()
    }
  }
}