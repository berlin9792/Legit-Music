package legit.music.ayush.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import legit.music.ayush.utils.NetworkConnectivityObserver

@Composable
fun NetworkReload(onReload: () -> Unit) {
  val context = LocalContext.current
  LaunchedEffect(context) {
    val observer = NetworkConnectivityObserver(context.applicationContext)
    var wasOffline = false
    try {
      observer.networkStatus.collect { isConnected ->
        if (isConnected) {
          if (wasOffline) {
            onReload()
          }
          wasOffline = false
        } else {
          wasOffline = true
        }
      }
    } finally {
      observer.unregister()
    }
  }
}
