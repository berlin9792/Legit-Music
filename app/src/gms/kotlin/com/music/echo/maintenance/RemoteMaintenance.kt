package echo.music.iad1tya.maintenance

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MAINTENANCE_ENABLED_KEY = "maintenance_enabled"
private const val MAINTENANCE_MESSAGE_KEY = "maintenance_message"
private const val MAINTENANCE_CONTROL_TOPIC = "legitmusic-maintenance-control"
private const val MINIMUM_FETCH_INTERVAL_SECONDS = 60L

object RemoteMaintenance {
  private val initialized = AtomicBoolean(false)
  private val ready = AtomicBoolean(false)
  private val pendingForcedRefresh = AtomicBoolean(false)
  private val mutableState = MutableStateFlow(RemoteMaintenanceState(isChecking = true))

  val state: StateFlow<RemoteMaintenanceState> = mutableState.asStateFlow()

  fun onAppVisible() {
    if (!initialized.compareAndSet(false, true)) {
      if (ready.get()) refresh()
      return
    }

    val config = FirebaseRemoteConfig.getInstance()
    val settings =
      FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_SECONDS)
        .setFetchTimeoutInSeconds(5)
        .build()

    config.setConfigSettingsAsync(settings).addOnCompleteListener {
      config
        .setDefaultsAsync(
          mapOf(
            MAINTENANCE_ENABLED_KEY to false,
            MAINTENANCE_MESSAGE_KEY to "",
          )
        )
        .addOnCompleteListener {
          updateState(config, isChecking = true)
          ready.set(true)
          FirebaseMessaging.getInstance().subscribeToTopic(MAINTENANCE_CONTROL_TOPIC)
          refresh(force = pendingForcedRefresh.getAndSet(false))
        }
    }
  }

  fun refreshFromPush() {
    if (!ready.get()) {
      pendingForcedRefresh.set(true)
      return
    }
    refresh(force = true)
  }

  private fun refresh(force: Boolean = false) {
    val config = FirebaseRemoteConfig.getInstance()
    val fetchTask = if (force) config.fetch(0) else config.fetch()

    fetchTask.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        config.activate().addOnCompleteListener { updateState(config, isChecking = false) }
      } else {
        updateState(config, isChecking = false)
      }
    }
  }

  private fun updateState(config: FirebaseRemoteConfig, isChecking: Boolean) {
    mutableState.value =
      RemoteMaintenanceState(
        enabled = config.getBoolean(MAINTENANCE_ENABLED_KEY),
        message = config.getString(MAINTENANCE_MESSAGE_KEY),
        isChecking = isChecking,
      )
  }
}