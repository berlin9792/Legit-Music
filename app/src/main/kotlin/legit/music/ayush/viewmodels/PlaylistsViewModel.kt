@file:OptIn(ExperimentalCoroutinesApi::class)

package legit.music.ayush.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import legit.music.ayush.constants.AddToPlaylistSortDescendingKey
import legit.music.ayush.constants.AddToPlaylistSortTypeKey
import legit.music.ayush.constants.PlaylistSortType
import legit.music.ayush.db.MusicDatabase
import legit.music.ayush.extensions.toEnum
import legit.music.ayush.utils.SyncUtils
import legit.music.ayush.utils.dataStore
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PlaylistsViewModel
@Inject
constructor(
  @ApplicationContext context: Context,
  database: MusicDatabase,
  private val syncUtils: SyncUtils,
) : ViewModel() {
  val allPlaylists =
    context.dataStore.data
      .map {
        (try {
            it[AddToPlaylistSortTypeKey]
          } catch (e: Exception) {
            null
          })
          .toEnum(PlaylistSortType.CREATE_DATE) to
          ((try {
            it[AddToPlaylistSortDescendingKey]
          } catch (e: Exception) {
            null
          }) ?: true)
      }
      .distinctUntilChanged()
      .flatMapLatest { (sortType, descending) -> database.playlists(sortType, descending) }
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  suspend fun sync() {
    syncUtils.syncSavedPlaylists()
  }
}
