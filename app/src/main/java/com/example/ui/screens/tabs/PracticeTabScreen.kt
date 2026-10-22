package com.example.ui.screens.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PointF
import com.example.data.model.SecurityConfig
import com.example.data.model.UnlockType
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LockUiState
import com.example.ui.viewmodel.LockViewModel

@Composable
fun PracticeTabScreen(
  uiState: LockUiState,
  viewModel: LockViewModel,
  onNavigateToArm: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    onNavigateToArm()
  }

  val scrollState = rememberScrollState()
  val drawnTestPoints = remember { mutableStateListOf<PointF>() }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 600.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TouchApp, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = stringResource(R.string.title_practice_sandbox),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = stringResource(R.string.desc_practice_sandbox),
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          // Tab Selector for Unlock Method
          val tabs = listOf(UnlockType.SYMBOL, UnlockType.TAP_COUNT, UnlockType.QUADRANT_COMBO)
          val selectedIndex = tabs.indexOf(uiState.config.activeUnlockType).coerceAtLeast(0)

          TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = CyberDarkSurfaceVariant,
            contentColor = CyberCyanPrimary,
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = CyberCyanPrimary,
                height = 3.dp
              )
            },
            divider = {}
          ) {
            tabs.forEachIndexed { index, type ->
              Tab(
                selected = selectedIndex == index,
                onClick = {
                  drawnTestPoints.clear()
                  viewModel.setUnlockType(type)
                  viewModel.setTestMode(true)
                },
                modifier = Modifier.testTag("practice_tab_${type.name}"),
                text = {
                  Text(
                    text = when (type) {
                      UnlockType.SYMBOL -> stringResource(R.string.tab_symbol)
                      UnlockType.TAP_COUNT -> stringResource(R.string.tab_taps)
                      UnlockType.QUADRANT_COMBO -> stringResource(R.string.tab_quadrants)
                    },
                    fontSize = 12.sp,
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                  )
                }
              )
            }
          }
        }
      }

      // Interactive Testing Pad
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberCyanPrimary.copy(alpha = 0.4f)))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stringResource(R.string.title_live_test),
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Button(
              onClick = { viewModel.openTraining(uiState.config.activeUnlockType) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003845), contentColor = CyberCyanPrimary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(stringResource(R.string.btn_edit_code), fontSize = 11.sp)
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(230.dp)
              .background(Color.Black, RoundedCornerShape(14.dp))
              .border(
                width = 1.5.dp,
                color = if (uiState.testFeedbackSuccess) CyberGreen else CyberCyanPrimary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
              )
              .pointerInput(uiState.config.activeUnlockType) {
                when (uiState.config.activeUnlockType) {
                  UnlockType.SYMBOL -> {
                    detectDragGestures(
                      onDragStart = { offset ->
                        drawnTestPoints.clear()
                        drawnTestPoints.add(PointF(offset.x, offset.y))
                      },
                      onDrag = { change, _ ->
                        change.consume()
                        drawnTestPoints.add(PointF(change.position.x, change.position.y))
                      },
                      onDragEnd = {
                        if (drawnTestPoints.size >= 4) {
                          viewModel.onGestureCompleted(drawnTestPoints.toList(), isTestMode = true)
                        }
                      }
                    )
                  }
                  UnlockType.TAP_COUNT -> {
                    awaitPointerEventScope {
                      while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Release) {
                          viewModel.onTapDetected(isTestMode = true)
                        }
                      }
                    }
                  }
                  UnlockType.QUADRANT_COMBO -> {
                    awaitPointerEventScope {
                      while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                          val pos = event.changes.first().position
                          val midX = size.width / 2f
                          val midY = size.height / 2f
                          val q = when {
                            pos.x < midX && pos.y < midY -> 1
                            pos.x >= midX && pos.y < midY -> 2
                            pos.x < midX && pos.y >= midY -> 3
                            else -> 4
                          }
                          viewModel.onQuadrantTouched(q, isTestMode = true)
                        }
                      }
                    }
                  }
                }
              }
              .testTag("practice_interactive_pad")
          ) {
            // Draw candidate stroke in symbol mode
            if (uiState.config.activeUnlockType == UnlockType.SYMBOL && drawnTestPoints.size >= 2) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                  moveTo(drawnTestPoints.first().x, drawnTestPoints.first().y)
                  for (i in 1 until drawnTestPoints.size) {
                    lineTo(drawnTestPoints[i].x, drawnTestPoints[i].y)
                  }
                }
                drawPath(path, color = CyberCyanPrimary, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
              }
            }

            // Quadrant divider lines
            if (uiState.config.activeUnlockType == UnlockType.QUADRANT_COMBO) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(Color.White.copy(alpha = 0.1f), start = Offset(size.width / 2f, 0f), end = Offset(size.width / 2f, size.height), strokeWidth = 1f)
                drawLine(Color.White.copy(alpha = 0.1f), start = Offset(0f, size.height / 2f), end = Offset(size.width, size.height / 2f), strokeWidth = 1f)
              }
            }

            Column(
              modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = when (uiState.config.activeUnlockType) {
                  UnlockType.SYMBOL -> stringResource(R.string.hint_draw_symbol)
                  UnlockType.TAP_COUNT -> stringResource(R.string.hint_tap_rhythm, uiState.config.savedTapCount)
                  UnlockType.QUADRANT_COMBO -> stringResource(R.string.hint_tap_quadrants, uiState.config.savedQuadrantCombo.joinToString(" \u2192 ") { "Q$it" })
                },
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
              )

              if (uiState.testFeedback != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = uiState.testFeedback,
                  color = if (uiState.testFeedbackSuccess) CyberGreen else CyberAmber,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center
                )
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                drawnTestPoints.clear()
                viewModel.setTestMode(true)
              },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
              Text(stringResource(R.string.btn_clear_pad), fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = { viewModel.triggerDeadBatteryFlash() },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
              modifier = Modifier.testTag("practice_test_battery_flash")
            ) {
              Icon(Icons.Default.BatteryAlert, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(stringResource(R.string.btn_test_battery_flash), fontSize = 11.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
