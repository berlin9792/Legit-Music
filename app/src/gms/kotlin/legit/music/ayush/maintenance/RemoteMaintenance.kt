package legit.music.ayush.maintenance

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

private const val MAINTENANCE_ENABLED_KEY = "legit_maintenance_enabled"
private const val MAINTENANCE_MESSAGE_KEY = "maintenance_message"
private const val MAINTENANCE_CONTROL_TOPIC = "legitmusic-maintenance-control"
private const val MINIMUM_FETCH_INTERVAL_SECONDS = 60L
private const val TAG = "RemoteMaintenance"

object RemoteMaintenance {
  private val initialized = AtomicBoolean(false)
  private val ready = AtomicBoolean(false)
  private val pendingForcedRefresh = AtomicBoolean(false)
  private val mutableState = MutableStateFlow(RemoteMaintenanceState(isChecking = true))

  val state: StateFlow<RemoteMaintenanceState> = mutableState.asStateFlow()

  fun onAppVisible() {
    if (!initialized.compareAndSet(false, true)) {
      if (ready.get()) {
        subscribeToControlTopic()
        refresh()
      }
      return
    }

    val config = FirebaseRemoteConfig.getInstance()
    val settings =
      FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_SECONDS)
        .setFetchTimeoutInSeconds(5)
        .build()

    config.setConfigSettingsAsync(settings).addOnCompleteListener { settingsTask ->
      if (!settingsTask.isSuccessful) {
        Timber.tag(TAG).e(settingsTask.exception, "Failed to configure Remote Config")
      }
      config
        .setDefaultsAsync(
          mapOf(
            MAINTENANCE_ENABLED_KEY to false,
            MAINTENANCE_MESSAGE_KEY to "",
          )
        )
        .addOnCompleteListener { defaultsTask ->
          if (!defaultsTask.isSuccessful) {
            Timber.tag(TAG).e(defaultsTask.exception, "Failed to set Remote Config defaults")
          }
          updateState(config, isChecking = true)
          ready.set(true)
          subscribeToControlTopic()
          refresh(force = pendingForcedRefresh.getAndSet(false))
        }
    }
  }

  fun refreshFromPush() {
    Timber.tag(TAG).d("Maintenance refresh push received")
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
        config.activate().addOnCompleteListener { activateTask ->
          if (!activateTask.isSuccessful) {
            Timber.tag(TAG).e(activateTask.exception, "Failed to activate Remote Config")
          }
          updateState(config, isChecking = false)
        }
      } else {
        Timber.tag(TAG).e(task.exception, "Failed to fetch Remote Config")
        updateState(config, isChecking = false)
      }
    }
  }

  private fun subscribeToControlTopic() {
    FirebaseMessaging.getInstance()
      .subscribeToTopic(MAINTENANCE_CONTROL_TOPIC)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Timber.tag(TAG).d("Subscribed to maintenance control topic")
        } else {
          Timber.tag(TAG).e(task.exception, "Failed to subscribe to maintenance control topic")
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