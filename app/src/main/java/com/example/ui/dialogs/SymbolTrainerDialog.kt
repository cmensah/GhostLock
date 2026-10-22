package com.example.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PointF
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SymbolTrainerDialog(
  onDismiss: () -> Unit,
  onSaveSymbol: (List<PointF>) -> Unit
) {
  val drawnPoints = remember { mutableStateListOf<PointF>() }
  var hasDrawn by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("symbol_trainer_dialog"),
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
            text = "Record Secret Symbol",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_symbol_trainer")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Text(
          text = "Draw your secret shape in a single continuous stroke inside the grid below.",
          color = TextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Start,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Drawing Canvas
        Box(
          modifier = Modifier
            .size(260.dp)
            .background(CyberDarkSurfaceVariant, RoundedCornerShape(16.dp))
            .border(1.5.dp, CyberCyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("symbol_training_canvas")
        ) {
          Canvas(
            modifier = Modifier
              .matchParentSize()
              .pointerInput(Unit) {
                detectDragGestures(
                  onDragStart = { offset ->
                    drawnPoints.clear()
                    drawnPoints.add(PointF(offset.x, offset.y))
                    hasDrawn = true
                  },
                  onDrag = { change, _ ->
                    change.consume()
                    drawnPoints.add(PointF(change.position.x, change.position.y))
                  },
                  onDragEnd = {
                    // Stroke completed
                  }
                )
              }
          ) {
            // Background subtle grid
            val step = size.width / 4f
            for (i in 1..3) {
              drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(step * i, 0f),
                end = Offset(step * i, size.height),
                strokeWidth = 1f
              )
              drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, step * i),
                end = Offset(size.width, step * i),
                strokeWidth = 1f
              )
            }

            if (drawnPoints.size >= 2) {
              val path = Path().apply {
                moveTo(drawnPoints.first().x, drawnPoints.first().y)
                for (i in 1 until drawnPoints.size) {
                  lineTo(drawnPoints[i].x, drawnPoints[i].y)
                }
              }
              drawPath(
                path = path,
                color = CyberCyanPrimary,
                style = Stroke(
                  width = 6.dp.toPx(),
                  cap = StrokeCap.Round,
                  join = StrokeJoin.Round
                )
              )
            }
          }

          if (!hasDrawn) {
            Text(
              text = "Touch & draw here\n(e.g., Z, \u03b1, triangle, circle)",
              color = TextSecondary.copy(alpha = 0.6f),
              fontSize = 13.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.align(Alignment.Center)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              drawnPoints.clear()
              hasDrawn = false
            },
            modifier = Modifier
              .weight(1f)
              .testTag("reset_symbol_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Clear", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text("Clear")
          }

          Button(
            onClick = {
              if (drawnPoints.size >= 4) {
                onSaveSymbol(drawnPoints.toList())
              }
            },
            enabled = drawnPoints.size >= 4,
            modifier = Modifier
              .weight(1.5f)
              .testTag("save_symbol_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = CyberCyanPrimary,
              contentColor = Color.Black
            )
          ) {
            Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text("Save Symbol")
          }
        }
      }
    }
  }
}
