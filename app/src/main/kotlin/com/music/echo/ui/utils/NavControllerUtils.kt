package echo.music.iad1tya.ui.utils

import androidx.navigation.NavController
import echo.music.iad1tya.ui.screens.Screens

fun NavController.backToMain() {
  val mainRoutes = Screens.MainScreens.map { it.route }

  while (
    previousBackStackEntry != null && currentBackStackEntry?.destination?.route !in mainRoutes
  ) {
    popBackStack()
  }
}

fun NavController.returnToMainAfterLogin() {
  backToMain()
  if (currentBackStackEntry?.destination?.route !in Screens.MainScreens.map { it.route }) {
    navigate(Screens.Home.route) {
      popUpTo("login") { inclusive = true }
      launchSingleTop = true
    }
  }
}
