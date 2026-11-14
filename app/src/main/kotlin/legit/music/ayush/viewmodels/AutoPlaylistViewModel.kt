package legit.music.ayush.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import legit.music.ayush.constants.ExportedSongIdsKey
import legit.music.ayush.constants.DownloadedSongSortTypeKey
import legit.music.ayush.constants.DownloadedSongSortDescendingKey
import legit.music.ayush.constants.HideExplicitKey
import legit.music.ayush.constants.HideVideoSongsKey
import legit.music.ayush.constants.SongSortDescendingKey
import legit.music.ayush.constants.SongSortType
import legit.music.ayush.constants.SongSortTypeKey
import legit.music.ayush.db.MusicDatabase
import legit.music.ayush.extensions.filterExplicit
import legit.music.ayush.extensions.filterVideoSongs
import legit.music.ayush.extensions.toEnum
import legit.music.ayush.utils.SyncUtils
import legit.music.ayush.utils.dataStore
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AutoPlaylistViewModel
@Inject
constructor(
  @ApplicationContext context: Context,
  private val database: MusicDatabase,
  savedStateHandle: SavedStateHandle,
  private val syncUtils: SyncUtils,
) : ViewModel() {
  val playlist = savedStateHandle.get<String>("playlist")!!
  private val sortTypeKey = if (playlist == "downloaded") DownloadedSongSortTypeKey else SongSortTypeKey
  private val sortDescendingKey =
    if (playlist == "downloaded") DownloadedSongSortDescendingKey else SongSortDescendingKey

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing = _isRefreshing.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val likedSongs =
    context.dataStore.data
      .map {
        Triple(
          Triple(
            (try {
                it[sortTypeKey]
              } catch (e: Exception) {
                null
              })
              .toEnum(SongSortType.CREATE_DATE) to
              ((try {
                it[sortDescendingKey]
              } catch (e: Exception) {
                null
              }) ?: true),
            (try {
              it[HideExplicitKey]
            } catch (e: Exception) {
              null
            }) ?: false,
            (try {
              it[HideVideoSongsKey]
            } catch (e: Exception) {
              null
            }) ?: false
          ),
          (try {
            it[ExportedSongIdsKey]
          } catch (e: Exception) {
            null
          }) ?: "",
          Unit
        )
      }
      .distinctUntilChanged()
      .flatMapLatest { (triple, exportedSongIds, _) ->
        val (sortDesc, hideExplicit, hideVideoSongs) = triple
        val (sortType, descending) = sortDesc
        when (playlist) {
          "liked" ->
            database.likedSongs(sortType, descending).map {
              it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs)
            }
          "downloaded" ->
            database.downloadedSongs(sortType, descending).map {
              it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs)
            }
          "uploaded" ->
            database.uploadedSongs(sortType, descending).map {
              it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs)
            }
          "exported" -> {
            val ids = exportedSongIds.split(",").filter { it.isNotBlank() }
            database.getSongsByIdsFlow(ids).map {
              it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs)
            }
          }
          else -> kotlinx.coroutines.flow.flowOf(emptyList())
        }
      }
      .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

  fun syncLikedSongs() {
    viewModelScope.launch(Dispatchers.IO) { syncUtils.syncLikedSongs() }
  }

  fun syncUploadedSongs() {
    viewModelScope.launch(Dispatchers.IO) { syncUtils.syncUploadedSongs() }
  }

  fun refresh() {
    viewModelScope.launch(Dispatchers.IO) {
      _isRefreshing.value = true
      when (playlist) {
        "liked" -> syncUtils.syncLikedSongsSuspend()
        "uploaded" -> syncUtils.syncUploadedSongsSuspend()
      }
      _isRefreshing.value = false
    }
  }
}
