package legit.music.ayush.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.pages.BrowseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import legit.music.ayush.constants.HideExplicitKey
import legit.music.ayush.constants.HideVideoSongsKey
import legit.music.ayush.constants.HideYoutubeShortsKey
import legit.music.ayush.utils.dataStore
import legit.music.ayush.utils.get
import legit.music.ayush.utils.reportException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class YouTubeBrowseViewModel
@Inject
constructor(
  @ApplicationContext val context: Context,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val browseId = savedStateHandle.get<String>("browseId")!!
  private val params = savedStateHandle.get<String>("params")

  val result = MutableStateFlow<BrowseResult?>(null)

  init {
    viewModelScope.launch {
      val hideExplicit = context.dataStore.get(HideExplicitKey, false)
      val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false)
      val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
      YouTube.browse(browseId, params)
        .onSuccess {
          result.value =
            it
              .filterExplicit(hideExplicit)
              .filterVideoSongs(hideVideoSongs)
              .filterYoutubeShorts(hideYoutubeShorts)
        }
        .onFailure { reportException(it) }
    }
  }
}
