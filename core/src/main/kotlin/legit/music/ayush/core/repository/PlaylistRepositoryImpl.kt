package legit.music.ayush.core.repository

import legit.music.ayush.core.mapping.toDomain
import legit.music.ayush.domain.models.Playlist as DomainPlaylist
import legit.music.ayush.domain.models.Song as DomainSong
import legit.music.ayush.domain.repositories.PlaylistRepository
import legit.music.ayush.db.MusicDatabase
import legit.music.ayush.db.entities.PlaylistSongMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
  private val database: MusicDatabase
) : PlaylistRepository {

  override fun getAllPlaylists(): Flow<List<DomainPlaylist>> {
    return database.playlistsByNameAsc().map { list ->
      list.map { it.toDomain() }
    }
  }

  override fun getPlaylistSongs(playlistId: String): Flow<List<DomainSong>> {
    return database.playlistSongs(playlistId).map { list ->
      list.map { it.song.toDomain() }
    }
  }

  override suspend fun addSongToPlaylist(playlistId: String, songId: String) {
    database.query {
      val existingMaps = playlistSongMaps(playlistId, 0)
      val nextPos = (existingMaps.maxOfOrNull { it.position } ?: -1) + 1
      insert(PlaylistSongMap(songId = songId, playlistId = playlistId, position = nextPos))
    }
  }

  override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
    database.query {
      val maps = playlistSongMaps(songId).filter { it.playlistId == playlistId }
      maps.forEach { delete(it) }
    }
  }
}
