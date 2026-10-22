package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class SupportedLanguage(
  val tag: String,
  val nativeName: String,
  val englishName: String,
  val flagEmoji: String
)

val APP_SUPPORTED_LANGUAGES = listOf(
  SupportedLanguage(tag = "", nativeName = "System Default", englishName = "Follow Android Settings", flagEmoji = "🌐"),
  SupportedLanguage(tag = "en", nativeName = "English", englishName = "English", flagEmoji = "🇬🇧"),
  SupportedLanguage(tag = "fr", nativeName = "Français", englishName = "French", flagEmoji = "🇫🇷"),
  SupportedLanguage(tag = "es", nativeName = "Español", englishName = "Spanish", flagEmoji = "🇪🇸"),
  SupportedLanguage(tag = "de", nativeName = "Deutsch", englishName = "German", flagEmoji = "🇩🇪"),
  SupportedLanguage(tag = "pt", nativeName = "Português", englishName = "Portuguese", flagEmoji = "🇵🇹")
)

@Composable
fun LanguageSelectorDialog(
  currentTag: String,
  onSelectLanguage: (String) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .testTag("dialog_language_selector"),
      colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
      shape = RoundedCornerShape(20.dp),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberCyanPrimary.copy(alpha = 0.5f)))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(Color(0xFF003845), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Language, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = stringResource(R.string.title_language),
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = stringResource(R.string.desc_language),
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          APP_SUPPORTED_LANGUAGES.forEach { lang ->
            val isSelected = currentTag == lang.tag
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  color = if (isSelected) Color(0xFF002933) else CyberDarkSurfaceVariant,
                  shape = RoundedCornerShape(12.dp)
                )
                .border(
                  width = 1.dp,
                  color = if (isSelected) CyberCyanPrimary else CyberOutline,
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                  onSelectLanguage(lang.tag)
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("lang_option_${if (lang.tag.isEmpty()) "system" else lang.tag}"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = lang.flagEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = if (lang.tag.isEmpty()) stringResource(R.string.lang_system_default) else lang.nativeName,
                    color = if (isSelected) CyberCyanPrimary else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = lang.englishName,
                    color = TextSecondary,
                    fontSize = 11.sp
                  )
                }
              }

              if (isSelected) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .background(CyberCyanPrimary, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                }
              }
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("btn_close_language_dialog")
          ) {
            Text(text = stringResource(R.string.btn_close), fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
