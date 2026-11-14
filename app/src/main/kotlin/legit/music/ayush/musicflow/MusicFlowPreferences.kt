package legit.music.ayush.musicflow

import androidx.datastore.preferences.core.Preferences
import legit.music.ayush.constants.CanvasThumbnailAnimationKey
import legit.music.ayush.constants.DataSaverEnabledKey
import legit.music.ayush.constants.MusicFlowAnimatedBackgroundKey
import legit.music.ayush.constants.MusicFlowAutoNextKey
import legit.music.ayush.constants.MusicFlowAutoplayKey
import legit.music.ayush.constants.MusicFlowCellularPrefetchKey
import legit.music.ayush.constants.MusicFlowEnabledKey
import legit.music.ayush.constants.MusicFlowHideRecentKey
import legit.music.ayush.constants.MusicFlowLyricsKey

data class MusicFlowPreferences(
  val enabled: Boolean = true,
  val autoplay: Boolean = true,
  val autoNext: Boolean = true,
  val animatedBackground: Boolean = false,
  val lyrics: Boolean = true,
  val cellularPrefetch: Boolean = true,
  val hideRecent: Boolean = true,
  val dataSaver: Boolean = false,
) {
  companion object {
    fun from(preferences: Preferences) =
      MusicFlowPreferences(
        enabled = preferences[MusicFlowEnabledKey] ?: true,
        autoplay = preferences[MusicFlowAutoplayKey] ?: true,
        autoNext = preferences[MusicFlowAutoNextKey] ?: true,
        animatedBackground =
          preferences[MusicFlowAnimatedBackgroundKey]
            ?: preferences[CanvasThumbnailAnimationKey]
            ?: false,
        lyrics = preferences[MusicFlowLyricsKey] ?: true,
        cellularPrefetch = preferences[MusicFlowCellularPrefetchKey] ?: true,
        hideRecent = preferences[MusicFlowHideRecentKey] ?: true,
        dataSaver = preferences[DataSaverEnabledKey] ?: false,
      )
  }
}
