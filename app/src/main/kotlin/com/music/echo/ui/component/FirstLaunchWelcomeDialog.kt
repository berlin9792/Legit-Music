package echo.music.iad1tya.ui.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import echo.music.iad1tya.R

private const val DEVELOPER_WEBSITE = "https://zerotracelegit.netlify.app"

@Composable
fun FirstLaunchWelcomeDialog(onDismiss: () -> Unit) {
  val context = LocalContext.current
  val uriHandler = LocalUriHandler.current
  val developerAvatar =
    remember(context) {
      checkNotNull(BitmapFactory.decodeResource(context.resources, R.drawable.dev)) {
        "Failed to decode developer avatar resource"
      }.asImageBitmap()
    }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 0.dp,
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Image(
          bitmap = developerAvatar,
          contentDescription = stringResource(R.string.welcome_developer_avatar_desc),
          modifier = Modifier.size(96.dp).clip(CircleShape),
        )
        Spacer(Modifier.height(20.dp))
        Text(
          text = stringResource(R.string.welcome_dialog_title),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
          text = stringResource(R.string.welcome_dialog_credit),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = stringResource(R.string.welcome_developer_name),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { uriHandler.openUri(DEVELOPER_WEBSITE) }) {
          Text(stringResource(R.string.welcome_website))
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
          Text(stringResource(R.string.welcome_get_started))
        }
      }
    }
  }
}
