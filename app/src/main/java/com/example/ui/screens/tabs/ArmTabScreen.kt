package com.example.ui.screens.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SecurityConfig
import com.example.data.model.SecurityEvent
import com.example.data.model.UnlockType
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ArmTabScreen(
  config: SecurityConfig,
  countdown: Int?,
  recentLogs: List<SecurityEvent>,
  onLockNow: (immediate: Boolean) -> Unit,
  onCancelCountdown: () -> Unit,
  onOpenTrainer: (UnlockType) -> Unit,
  onViewLogs: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

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
      // Main Arming Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("arm_status_card"),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(CyberCyanPrimary.copy(alpha = 0.5f))
        )
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .background(CyberGreen, CircleShape)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = stringResource(R.string.status_shield_ready),
                color = CyberGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Box(
              modifier = Modifier
                .background(Color(0xFF003845), RoundedCornerShape(8.dp))
                .border(1.dp, CyberCyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = when (config.activeUnlockType) {
                  UnlockType.SYMBOL -> stringResource(R.string.tab_symbol)
                  UnlockType.TAP_COUNT -> stringResource(R.string.tab_taps)
                  UnlockType.QUADRANT_COMBO -> stringResource(R.string.tab_quadrants)
                },
                color = CyberCyanPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (countdown != null) {
            // Countdown in progress
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF33200D), RoundedCornerShape(12.dp))
                .border(1.dp, CyberAmber, RoundedCornerShape(12.dp))
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "$countdown",
                  color = CyberAmber,
                  fontSize = 28.sp,
                  fontWeight = FontWeight.ExtraBold,
                  fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = stringResource(R.string.msg_engaging_lock),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = stringResource(R.string.msg_turning_black),
                    color = TextSecondary,
                    fontSize = 11.sp
                  )
                }
              }

              IconButton(
                onClick = onCancelCountdown,
                modifier = Modifier.testTag("cancel_countdown_button")
              ) {
                Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = CyberAmber)
              }
            }
          } else {
            // Arm Lock Action Button
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { onLockNow(false) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("btn_lock_now"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = CyberCyanPrimary,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp)
              ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = stringResource(R.string.btn_arm_and_engage),
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp,
                  letterSpacing = 1.sp
                )
              }

              if (config.armDelaySeconds > 0) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = stringResource(R.string.delay_configured, config.armDelaySeconds),
                    color = TextSecondary,
                    fontSize = 11.sp
                  )
                  TextButton(
                    onClick = { onLockNow(true) },
                    modifier = Modifier.testTag("btn_lock_instant")
                  ) {
                    Text(
                      text = stringResource(R.string.btn_lock_immediate),
                      color = CyberCyanPrimary,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Active Unlock Summary & Quick Redraw Card
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
          Text(
            text = stringResource(R.string.title_active_secret),
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(CyberDarkSurfaceVariant, RoundedCornerShape(12.dp))
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            when (config.activeUnlockType) {
              UnlockType.SYMBOL -> {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberOutline, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Canvas(modifier = Modifier.size(38.dp)) {
                    val pts = config.savedSymbolPoints
                    if (pts.size >= 2) {
                      val path = Path().apply {
                        moveTo(pts.first().x * size.width, pts.first().y * size.height)
                        for (i in 1 until pts.size) {
                          lineTo(pts[i].x * size.width, pts[i].y * size.height)
                        }
                      }
                      drawPath(
                        path,
                        color = CyberCyanPrimary,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(stringResource(R.string.unlock_symbol), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text(stringResource(R.string.unlock_symbol_desc), color = TextSecondary, fontSize = 11.sp)
                }
                Button(
                  onClick = { onOpenTrainer(UnlockType.SYMBOL) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003845), contentColor = CyberCyanPrimary)
                ) {
                  Text(stringResource(R.string.btn_redraw), fontSize = 11.sp)
                }
              }
              UnlockType.TAP_COUNT -> {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberOutline, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${config.savedTapCount}",
                    color = CyberCyanPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(stringResource(R.string.unlock_taps_format, config.savedTapCount), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text(stringResource(R.string.unlock_taps_desc), color = TextSecondary, fontSize = 11.sp)
                }
                Button(
                  onClick = { onOpenTrainer(UnlockType.TAP_COUNT) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003845), contentColor = CyberCyanPrimary)
                ) {
                  Text(stringResource(R.string.btn_change), fontSize = 11.sp)
                }
              }
              UnlockType.QUADRANT_COMBO -> {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberOutline, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.GridOn, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(config.savedQuadrantCombo.joinToString(" \u2192 ") { "Q$it" }, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text(stringResource(R.string.unlock_quadrants_desc), color = TextSecondary, fontSize = 11.sp)
                }
                Button(
                  onClick = { onOpenTrainer(UnlockType.QUADRANT_COMBO) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003845), contentColor = CyberCyanPrimary)
                ) {
                  Text(stringResource(R.string.btn_change), fontSize = 11.sp)
                }
              }
            }
          }
        }
      }

      // Quick Security Status & Audit Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.History, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(stringResource(R.string.title_security_activity), color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onViewLogs) {
              Text(stringResource(R.string.btn_view_all_logs), color = CyberCyanPrimary, fontSize = 11.sp)
            }
          }

          if (recentLogs.isEmpty()) {
            Text(
              text = stringResource(R.string.msg_no_logs),
              color = TextSecondary,
              fontSize = 12.sp
            )
          } else {
            recentLogs.take(2).forEach { event ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(CyberDarkSurfaceVariant, RoundedCornerShape(8.dp))
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(CyberGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = event.details,
                  color = TextPrimary,
                  fontSize = 12.sp,
                  maxLines = 1,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
