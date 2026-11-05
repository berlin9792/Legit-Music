package echo.music.iad1tya.maintenance

data class RemoteMaintenanceState(
  val enabled: Boolean = false,
  val message: String = "",
  val isChecking: Boolean = false,
)