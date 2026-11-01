package echo.music.iad1tya.discord

import android.content.Context
import androidx.datastore.preferences.core.edit
import echo.music.iad1tya.BuildConfig
import echo.music.iad1tya.constants.DiscordRefreshTokenKey
import echo.music.iad1tya.constants.DiscordTokenExpiresAtKey
import echo.music.iad1tya.constants.DiscordTokenKey
import echo.music.iad1tya.utils.dataStore
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object DiscordTokenProvider {
  private const val TOKEN_ENDPOINT = "https://discord.com/api/oauth2/token"
  private const val REQUEST_TIMEOUT_MS = 12_000
  private const val EXPIRY_SKEW_MS = 60_000L

  private val json = Json { ignoreUnknownKeys = true }

  suspend fun getValidAccessToken(context: Context): String? =
    withContext(Dispatchers.IO) {
      val prefs = context.dataStore.data.first()
      val currentToken = prefs[DiscordTokenKey]?.trim().orEmpty()
      if (currentToken.isBlank()) {
        return@withContext null
      }

      val expiresAt = prefs[DiscordTokenExpiresAtKey] ?: 0L
      if (expiresAt == 0L || System.currentTimeMillis() + EXPIRY_SKEW_MS < expiresAt) {
        return@withContext currentToken
      }

      val refreshToken = prefs[DiscordRefreshTokenKey]?.trim().orEmpty()
      if (refreshToken.isBlank()) {
        return@withContext currentToken
      }

      refreshAccessToken(context, refreshToken).getOrNull() ?: currentToken
    }

  private suspend fun refreshAccessToken(
    context: Context,
    refreshToken: String,
  ): Result<String> =
    withContext(Dispatchers.IO) {
      runCatching {
        val token =
          postForm(
            url = TOKEN_ENDPOINT,
            params =
              mapOf(
                "client_id" to BuildConfig.DISCORD_APPLICATION_ID,
                "grant_type" to "refresh_token",
                "refresh_token" to refreshToken,
              ),
          )
            .let { json.decodeFromString<TokenResponse>(it) }
        val expiresAtMillis =
          if (token.expiresInSeconds > 0L) {
            System.currentTimeMillis() + token.expiresInSeconds * 1000L
          } else {
            0L
          }

        context.dataStore.edit { preferences ->
          preferences[DiscordTokenKey] = token.accessToken
          token.refreshToken?.takeIf { it.isNotBlank() }?.let {
            preferences[DiscordRefreshTokenKey] = it
          }
          preferences[DiscordTokenExpiresAtKey] = expiresAtMillis
        }

        token.accessToken
      }
    }

  private fun postForm(
    url: String,
    params: Map<String, String>,
  ): String {
    val body =
      params.entries.joinToString(separator = "&") { (key, value) ->
        "${key.urlEncode()}=${value.urlEncode()}"
      }
    val connection =
      (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        connectTimeout = REQUEST_TIMEOUT_MS
        readTimeout = REQUEST_TIMEOUT_MS
        doOutput = true
        setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        setRequestProperty("Accept", "application/json")
      }

    connection.outputStream.use { output -> output.write(body.toByteArray(Charsets.UTF_8)) }

    return connection.readResponse()
  }

  private fun HttpURLConnection.readResponse(): String {
    val status = responseCode
    val stream = if (status in 200..299) inputStream else errorStream
    val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
    disconnect()

    if (status !in 200..299) {
      throw IOException("Discord OAuth request failed with HTTP $status: $body")
    }

    return body
  }

  private fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8.name())

  @Serializable
  private data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresInSeconds: Long = 0L,
  )
}