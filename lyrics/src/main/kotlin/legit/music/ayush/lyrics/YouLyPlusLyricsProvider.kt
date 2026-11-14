package legit.music.ayush.lyrics

import android.content.Context
import com.music.youlyplus.YouLyPlus
import legit.music.ayush.constants.EnableYouLyPlusKey
import legit.music.ayush.utils.dataStore
import legit.music.ayush.utils.get

object YouLyPlusLyricsProvider : LyricsProvider {
  override val name = "YouLyPlus"

  override fun isEnabled(context: Context): Boolean = context.dataStore[EnableYouLyPlusKey] ?: true

  override suspend fun getLyrics(
    id: String,
    title: String,
    artist: String,
    duration: Int,
    album: String?,
  ): Result<String> = YouLyPlus.getLyrics(title, artist, duration, album, id)

  override suspend fun getAllLyrics(
    id: String,
    title: String,
    artist: String,
    duration: Int,
    album: String?,
    callback: (String) -> Unit,
  ) {
    YouLyPlus.getAllLyrics(title, artist, duration, album, id, null, callback)
  }
}
