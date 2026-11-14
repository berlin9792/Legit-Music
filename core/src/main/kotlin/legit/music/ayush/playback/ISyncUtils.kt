package legit.music.ayush.playback

import legit.music.ayush.db.entities.SongEntity

interface ISyncUtils {
  fun likeSong(song: SongEntity)
}
