package com.example.ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun TapTrainerDialog(
  initialTapCount: Int,
  onDismiss: () -> Unit,
  onSaveTapCount: (Int) -> Unit
) {
  var tapCount by remember { mutableIntStateOf(initialTapCount.coerceIn(2, 10)) }
  var testTapCount by remember { mutableIntStateOf(0) }
  val scaleAnim = remember { Animatable(1f) }
  val scope = rememberCoroutineScope()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("tap_trainer_dialog"),
      colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
      shape = RoundedCornerShape(20.dp),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Configure Tap Sequence",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_tap_trainer")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Text(
          text = "Select the exact number of consecutive rhythmic taps required to unlock the black screen.",
          color = TextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Start,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tap Count Adjuster
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(CyberDarkSurfaceVariant, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              if (tapCount > 2) {
                tapCount--
                testTapCount = 0
              }
            },
            enabled = tapCount > 2,
            modifier = Modifier.testTag("decrement_tap_count")
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = if (tapCount > 2) CyberCyanPrimary else TextSecondary.copy(alpha = 0.3f))
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$tapCount TAPS",
              color = CyberCyanPrimary,
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold
            )
            Text(
              text = "Secret Tap Count",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }

          IconButton(
            onClick = {
              if (tapCount < 10) {
                tapCount++
                testTapCount = 0
              }
            },
            enabled = tapCount < 10,
            modifier = Modifier.testTag("increment_tap_count")
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", tint = if (tapCount < 10) CyberCyanPrimary else TextSecondary.copy(alpha = 0.3f))
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Practice Pad
        Text(
          text = "Practice Tap Cadence:",
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(CyberDarkSurfaceVariant, RoundedCornerShape(16.dp))
            .border(
              width = 1.5.dp,
              color = if (testTapCount == tapCount) CyberGreen else CyberOutline,
              shape = RoundedCornerShape(16.dp)
            )
            .clickable {
              scope.launch {
                scaleAnim.animateTo(0.95f, tween(60))
                scaleAnim.animateTo(1f, tween(60))
              }
              testTapCount = (testTapCount + 1) % (tapCount + 1)
            }
            .scale(scaleAnim.value)
            .testTag("tap_practice_pad"),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              Icons.Default.TouchApp,
              contentDescription = "Tap Pad",
              tint = if (testTapCount == tapCount) CyberGreen else CyberCyanPrimary,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (testTapCount == tapCount) "Unlock Triggered! ($tapCount taps)" else "Taps registered: $testTapCount / $tapCount",
              color = if (testTapCount == tapCount) CyberGreen else TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Tap rhythmically to practice",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = { onSaveTapCount(tapCount) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("save_tap_count_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = CyberCyanPrimary,
            contentColor = Color.Black
          )
        ) {
          Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.size(8.dp))
          Text("Set as $tapCount Taps Code")
        }
      }
    }
  }
}
