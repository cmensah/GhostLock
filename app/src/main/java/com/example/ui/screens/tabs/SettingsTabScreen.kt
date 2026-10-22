package com.example.ui.screens.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FailsafeCorner
import com.example.data.model.SecurityConfig
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LockViewModel

@Composable
fun SettingsTabScreen(
  config: SecurityConfig,
  viewModel: LockViewModel,
  onNavigateToArm: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    onNavigateToArm()
  }

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
      // Emergency Fail-Safe Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("failsafe_settings_card"),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberAmber.copy(alpha = 0.5f)))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Key, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Emergency Fail-Safe & Override",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Recover access if you ever forget your secret gesture or pattern",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          Text(
            text = "Hold down the secret corner for 3 seconds on the dark screen to summon the master PIN override keypad.",
            color = TextSecondary,
            fontSize = 12.sp
          )

          // Corner Selector
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Secret Fail-Safe Corner:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              FailsafeCorner.values().forEach { corner ->
                val isSelected = config.failsafeCorner == corner
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .background(
                      color = if (isSelected) Color(0xFF332600) else CyberDarkSurfaceVariant,
                      shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                      width = 1.dp,
                      color = if (isSelected) CyberAmber else CyberOutline,
                      shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.setFailsafeCorner(corner) }
                    .padding(vertical = 10.dp)
                    .testTag("corner_${corner.name}"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = corner.shortName,
                    color = if (isSelected) CyberAmber else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          // Master Backup PIN
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(CyberDarkSurfaceVariant, RoundedCornerShape(12.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Master Backup PIN", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Configured: \u2022\u2022\u2022\u2022 (${config.backupPin.length} digits)", color = TextSecondary, fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = { viewModel.setShowPinConfigDialog(true) },
              modifier = Modifier.testTag("change_pin_button"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberAmber)
            ) {
              Text("Change PIN", fontSize = 11.sp)
            }
          }
        }
      }

      // Stealth & Lockdown Controls
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = "Stealth & Hardware Button Lockdown",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )

          // Fake Empty Battery on Tamper (🪫)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BatteryAlert, contentDescription = null, tint = Color(0xFFFF3333), modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Fake Empty Battery on Tamper (🪫)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Flashes depleted 0% battery on buttons or wrong codes, then goes dark", color = TextSecondary, fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.showDeadBatteryOnTamper,
              onCheckedChange = { viewModel.toggleShowDeadBatteryOnTamper(it) },
              modifier = Modifier.testTag("toggle_dead_battery_switch")
            )
          }

          // Lock Task Mode (Screen Pinning)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Lock Task Mode (Disable Home & Recents)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Calls OS-level App Pinning to freeze Home, Recents, and Status bar", color = TextSecondary, fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.enableLockTask,
              onCheckedChange = { viewModel.toggleEnableLockTask(it) },
              modifier = Modifier.testTag("toggle_lock_task_switch")
            )
          }

          // Instant Anti-Home Re-entry
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Security, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Instant Anti-Home Recapture", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Intercepts lifecycle on Home touch to instantly restore black screen", color = TextSecondary, fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.autoReopenOnHome,
              onCheckedChange = { viewModel.toggleAutoReopenOnHome(it) },
              modifier = Modifier.testTag("toggle_auto_reopen_switch")
            )
          }

          // Fake Shutdown Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BatteryAlert, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Fake Low-Battery Shutdown", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Displays simulated Android power-off screen before blacking out", color = TextSecondary, fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.enableFakeShutdown,
              onCheckedChange = { viewModel.toggleFakeShutdown(it) },
              modifier = Modifier.testTag("toggle_fake_shutdown_switch")
            )
          }

          // Dim Brightness
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BrightnessLow, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Dim OLED Brightness to 0%", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Reduces backlight to absolute minimum for realistic screen-off", color = TextSecondary, fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.dimBrightnessToMin,
              onCheckedChange = { viewModel.toggleDimBrightness(it) },
              modifier = Modifier.testTag("toggle_dim_brightness_switch")
            )
          }

          // Haptic Vibration on Unlock
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Vibration, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Haptic Buzz on Unlock", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Subtle vibration to confirm unlock without visual light", color = TextSecondary, fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.vibrateOnSuccess,
              onCheckedChange = { viewModel.toggleVibrateOnSuccess(it) },
              modifier = Modifier.testTag("toggle_vibrate_switch")
            )
          }

          // Arm Delay Selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Arming Delay", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Countdown before dark screen engages", color = TextSecondary, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(0, 3, 5, 10).forEach { sec ->
                val isSelected = config.armDelaySeconds == sec
                Box(
                  modifier = Modifier
                    .background(
                      color = if (isSelected) CyberCyanPrimary else CyberDarkSurfaceVariant,
                      shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { viewModel.setArmDelaySeconds(sec) }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("delay_${sec}s"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (sec == 0) "Instant" else "${sec}s",
                    color = if (isSelected) Color.Black else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // Audit Trail Launcher
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Security Audit Trail", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text("View all lock, unlock, and tamper history", color = TextSecondary, fontSize = 11.sp)
            }
          }

          OutlinedButton(
            onClick = { viewModel.setShowLogsDialog(true) },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyanPrimary)
          ) {
            Text("View Logs", fontSize = 11.sp)
          }
        }
      }

      // About & Open Source Card
      var showLicenseDialog by remember { mutableStateOf(false) }

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberCyanPrimary.copy(alpha = 0.3f)))
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .background(Color(0xFF003845), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("GhostLock v1.0.0", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Open Source Stealth Security", color = TextSecondary, fontSize = 11.sp)
              }
            }

            Box(
              modifier = Modifier
                .background(Color(0xFF132A1C), RoundedCornerShape(6.dp))
                .border(1.dp, CyberGreen.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text("Apache 2.0", color = CyberGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Text(
            text = "GhostLock is an open-source anti-tamper security tool. Zero network permissions, zero telemetry, and zero tracking. All secret gestures and PINs remain strictly encrypted on-device.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(CyberDarkSurfaceVariant, RoundedCornerShape(10.dp))
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("CI/CD Release Pipeline", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text("Automated APK builds on GitHub push", color = TextSecondary, fontSize = 10.sp)
            }

            OutlinedButton(
              onClick = { showLicenseDialog = true },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyanPrimary),
              modifier = Modifier.testTag("btn_view_license")
            ) {
              Text("License", fontSize = 11.sp)
            }
          }
        }
      }

      if (showLicenseDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showLicenseDialog = false }) {
          Card(
            modifier = Modifier
              .fillMaxWidth(0.95f)
              .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text("Apache License 2.0", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
              Text(
                text = "Copyright 2026 GhostLock Contributors\n\nLicensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License.\n\nYou may obtain a copy of the License at:\nhttp://www.apache.org/licenses/LICENSE-2.0\n\nThis software is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                androidx.compose.material3.Button(
                  onClick = { showLicenseDialog = false },
                  colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black)
                ) {
                  Text("Close", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
