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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuadrantTrainerDialog(
  initialCombo: List<Int>,
  onDismiss: () -> Unit,
  onSaveCombo: (List<Int>) -> Unit
) {
  val comboList = remember { mutableStateListOf<Int>().apply { addAll(initialCombo) } }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("quadrant_trainer_dialog"),
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
            text = "Record Quadrant Pattern",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_quadrant_trainer")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Text(
          text = "Tap the four screen quadrants below in your secret unlock order (2 to 8 steps).",
          color = TextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Start,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sequence Display
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(CyberDarkSurfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (comboList.isEmpty()) {
            Text(
              text = "Tap quadrants below to start pattern",
              color = TextSecondary,
              fontSize = 12.sp
            )
          } else {
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              comboList.forEachIndexed { idx, q ->
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .background(CyberCyanPrimary, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Q$q",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                if (idx < comboList.size - 1) {
                  Text(text = "\u2192", color = CyberAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2x2 Quadrant Grid
        Column(
          modifier = Modifier
            .size(240.dp)
            .background(CyberDarkSurfaceVariant, RoundedCornerShape(16.dp))
            .border(1.5.dp, CyberOutline, RoundedCornerShape(16.dp))
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuadrantTile(
              label = "Q1\nTop-Left",
              modifier = Modifier
                .weight(1f)
                .testTag("quadrant_tile_1"),
              onClick = { if (comboList.size < 8) comboList.add(1) }
            )
            QuadrantTile(
              label = "Q2\nTop-Right",
              modifier = Modifier
                .weight(1f)
                .testTag("quadrant_tile_2"),
              onClick = { if (comboList.size < 8) comboList.add(2) }
            )
          }
          Row(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuadrantTile(
              label = "Q3\nBottom-Left",
              modifier = Modifier
                .weight(1f)
                .testTag("quadrant_tile_3"),
              onClick = { if (comboList.size < 8) comboList.add(3) }
            )
            QuadrantTile(
              label = "Q4\nBottom-Right",
              modifier = Modifier
                .weight(1f)
                .testTag("quadrant_tile_4"),
              onClick = { if (comboList.size < 8) comboList.add(4) }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { comboList.clear() },
            modifier = Modifier
              .weight(1f)
              .testTag("reset_quadrants_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Clear", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text("Clear")
          }

          Button(
            onClick = {
              if (comboList.size >= 2) {
                onSaveCombo(comboList.toList())
              }
            },
            enabled = comboList.size >= 2,
            modifier = Modifier
              .weight(1.5f)
              .testTag("save_quadrants_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = CyberCyanPrimary,
              contentColor = Color.Black
            )
          ) {
            Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text("Save Pattern")
          }
        }
      }
    }
  }
}

@Composable
private fun QuadrantTile(
  label: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .background(Color(0xFF1B2433), RoundedCornerShape(12.dp))
      .border(1.dp, CyberOutline, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = TextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Center
    )
  }
}
