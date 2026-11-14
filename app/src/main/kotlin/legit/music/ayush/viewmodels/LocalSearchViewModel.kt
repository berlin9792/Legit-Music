package legit.music.ayush.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import legit.music.ayush.constants.HideVideoSongsKey
import legit.music.ayush.db.MusicDatabase
import legit.music.ayush.db.entities.Album
import legit.music.ayush.db.entities.Artist
import legit.music.ayush.db.entities.LocalItem
import legit.music.ayush.db.entities.Playlist
import legit.music.ayush.db.entities.Song
import legit.music.ayush.utils.dataStore
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LocalSearchViewModel
@Inject
constructor(
  @ApplicationContext context: Context,
  database: MusicDatabase,
) : ViewModel() {
  val query = MutableStateFlow("")
  val filter = MutableStateFlow(LocalFilter.ALL)

  val result =
    combine(
        query,
        filter,
        context.dataStore.data
          .map {
            (try {
              it[HideVideoSongsKey]
            } catch (e: Exception) {
              null
            }) ?: false
          }
          .distinctUntilChanged()
      ) { query, filter, hideVideoSongs ->
        Triple(query, filter, hideVideoSongs)
      }
      .flatMapLatest { (query, filter, hideVideoSongs) ->
        if (query.isEmpty()) {
          flowOf(LocalSearchResult("", filter, emptyMap()))
        } else {
          when (filter) {
            LocalFilter.ALL ->
              combine(
                database.searchSongs(query, PREVIEW_SIZE),
                database.searchAlbums(query, PREVIEW_SIZE),
                database.searchArtists(query, PREVIEW_SIZE),
                database.searchPlaylists(query, PREVIEW_SIZE),
              ) { songs, albums, artists, playlists ->
                val filteredSongs = if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                filteredSongs + albums + artists + playlists
              }
            LocalFilter.SONG ->
              database.searchSongs(query).map { songs ->
                if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
              }
            LocalFilter.ALBUM -> database.searchAlbums(query)
            LocalFilter.ARTIST -> database.searchArtists(query)
            LocalFilter.PLAYLIST -> database.searchPlaylists(query)
          }.map { list ->
            LocalSearchResult(
              query = query,
              filter = filter,
              map =
                list.groupBy {
                  when (it) {
                    is Song -> LocalFilter.SONG
                    is Album -> LocalFilter.ALBUM
                    is Artist -> LocalFilter.ARTIST
                    is Playlist -> LocalFilter.PLAYLIST
                  }
                },
            )
          }
        }
      }
      .stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        LocalSearchResult("", filter.value, emptyMap())
      )

  companion object {
    const val PREVIEW_SIZE = 3
  }
}

enum class LocalFilter {
  ALL,
  SONG,
  ALBUM,
  ARTIST,
  PLAYLIST,
}

data class LocalSearchResult(
  val query: String,
  val filter: LocalFilter,
  val map: Map<LocalFilter, List<LocalItem>>,
)
