package legit.music.ayush.maintenance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RemoteMaintenance {
  private val mutableState = MutableStateFlow(RemoteMaintenanceState())

  val state: StateFlow<RemoteMaintenanceState> = mutableState.asStateFlow()

  fun onAppVisible() = Unit

  fun refreshFromPush() = Unit
}