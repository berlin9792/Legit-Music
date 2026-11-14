package legit.music.ayush.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import legit.music.ayush.R

val LocalSettingsTileArtworkEnabled = staticCompositionLocalOf { false }

@Composable
fun SettingsArtworkContainer(
  route: String?,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  val isSettingsRoute = route == "settings" || route?.startsWith("settings/") == true
  val isPlayerOrEqualizerRoute =
    route == "settings/player" ||
      route?.startsWith("settings/player/") == true ||
      route == "settings/equalizer" ||
      route?.startsWith("settings/equalizer/") == true
  val useTileArtwork =
    isSettingsRoute && !isPlayerOrEqualizerRoute
  val colorScheme = MaterialTheme.colorScheme

  Box(modifier = modifier) {
    if (isSettingsRoute) {
      Image(
        painter = painterResource(R.drawable.settingbg),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    }

    CompositionLocalProvider(LocalSettingsTileArtworkEnabled provides useTileArtwork) {
      if (isSettingsRoute) {
        MaterialTheme(
          colorScheme =
            colorScheme.copy(
              surface = Color.Transparent,
              onSurface = Color.White,
              onSurfaceVariant = Color.White.copy(alpha = 0.84f),
              onBackground = Color.White,
            )
        ) {
          content()
        }
      } else {
        content()
      }
    }
  }
}
