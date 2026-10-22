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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.CyberRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EmergencyPinDialog(
  enteredPin: String,
  targetPinLength: Int,
  errorMessage: String?,
  onDigitPress: (Char) -> Unit,
  onBackspace: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.88f)
        .padding(16.dp)
        .testTag("emergency_pin_dialog"),
      colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
      shape = RoundedCornerShape(24.dp),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Master Override",
              tint = CyberCyanPrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Emergency Override",
              color = TextPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("dismiss_emergency_pin")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextSecondary)
          }
        }

        Text(
          text = "Fail-safe corner triggered. Enter master recovery PIN to exit stealth lockdown.",
          color = TextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Start,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 18.dp)
        )

        // PIN Indicator Dots
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 12.dp)
        ) {
          for (i in 0 until targetPinLength) {
            val isFilled = i < enteredPin.length
            Box(
              modifier = Modifier
                .size(16.dp)
                .background(
                  color = if (isFilled) CyberCyanPrimary else CyberDarkSurfaceVariant,
                  shape = CircleShape
                )
                .border(
                  width = 1.5.dp,
                  color = if (isFilled) CyberCyanPrimary else CyberOutline,
                  shape = CircleShape
                )
            )
          }
        }

        if (errorMessage != null) {
          Text(
            text = errorMessage,
            color = CyberRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad Grid 3x4
        val rows = listOf(
          listOf('1', '2', '3'),
          listOf('4', '5', '6'),
          listOf('7', '8', '9'),
          listOf('C', '0', '<')
        )

        Column(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          for (row in rows) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              for (key in row) {
                when (key) {
                  'C' -> {
                    Box(
                      modifier = Modifier
                        .size(62.dp)
                        .clickable { onDismiss() },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "CANCEL",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }
                  '<' -> {
                    Box(
                      modifier = Modifier
                        .size(62.dp)
                        .background(CyberDarkSurfaceVariant, CircleShape)
                        .border(1.dp, CyberOutline, CircleShape)
                        .clickable { onBackspace() }
                        .testTag("pin_backspace"),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                  else -> {
                    Box(
                      modifier = Modifier
                        .size(62.dp)
                        .background(CyberDarkSurfaceVariant, CircleShape)
                        .border(1.dp, CyberOutline, CircleShape)
                        .clickable { onDigitPress(key) }
                        .testTag("pin_key_$key"),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = key.toString(),
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
