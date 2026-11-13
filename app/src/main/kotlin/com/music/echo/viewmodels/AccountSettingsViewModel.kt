package echo.music.iad1tya.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.App
import echo.music.iad1tya.constants.AccountChannelHandleKey
import echo.music.iad1tya.constants.AccountEmailKey
import echo.music.iad1tya.constants.AccountNameKey
import echo.music.iad1tya.constants.DataSyncIdKey
import echo.music.iad1tya.constants.InnerTubeCookieKey
import echo.music.iad1tya.constants.VisitorDataKey
import echo.music.iad1tya.utils.InnerTubeXResolver
import echo.music.iad1tya.utils.SyncUtils
import echo.music.iad1tya.utils.dataStore
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AccountSettingsViewModel
@Inject
constructor(
  private val syncUtils: SyncUtils,
) : ViewModel() {

  fun logoutAndClearSyncedContent(context: Context, onCookieChange: (String) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      syncUtils.clearAllSyncedContent()

      App.forgetAccount(context)

      onCookieChange("")
    }
  }

  fun logoutKeepData(context: Context, onCookieChange: (String) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      App.forgetAccount(context)
      withContext(Dispatchers.Main) { onCookieChange("") }
    }
  }

  fun saveAccountSession(
    context: Context,
    cookie: String,
    visitorData: String,
    dataSyncId: String,
    accountName: String,
    accountEmail: String,
    accountChannelHandle: String,
    onSaved: () -> Unit,
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      context.dataStore.edit { settings ->
        settings[InnerTubeCookieKey] = cookie
        settings[VisitorDataKey] = visitorData
        settings[DataSyncIdKey] = dataSyncId
        settings[AccountNameKey] = accountName
        settings[AccountEmailKey] = accountEmail
        settings[AccountChannelHandleKey] = accountChannelHandle
      }
      YouTube.cookie = cookie
      YouTube.visitorData = visitorData
      YouTube.dataSyncId = dataSyncId
      InnerTubeXResolver.onSessionChanged()
      withContext(Dispatchers.Main) { onSaved() }
    }
  }
}
