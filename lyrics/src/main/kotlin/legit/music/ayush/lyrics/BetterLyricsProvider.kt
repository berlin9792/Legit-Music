package legit.music.ayush.lyrics

import android.content.Context
import legit.music.ayush.betterlyrics.BetterLyrics
import legit.music.ayush.constants.EnableBetterLyricsKey
import legit.music.ayush.utils.dataStore
import legit.music.ayush.utils.get

object BetterLyricsProvider : LyricsProvider {
  override val name = "BetterLyrics"

  override fun isEnabled(context: Context): Boolean =
    context.dataStore[EnableBetterLyricsKey] ?: true

  override suspend fun getLyrics(
    id: String,
    title: String,
    artist: String,
    duration: Int,
    album: String?,
  ): Result<String> = BetterLyrics.getLyrics(title, artist, duration, album)

  override suspend fun getAllLyrics(
    id: String,
    title: String,
    artist: String,
    duration: Int,
    album: String?,
    callback: (String) -> Unit,
  ) {
    BetterLyrics.getAllLyrics(title, artist, duration, album, callback)
  }
}
