package legit.music.ayush.maintenance

data class RemoteMaintenanceState(
  val enabled: Boolean = false,
  val message: String = "",
  val isChecking: Boolean = false,
)