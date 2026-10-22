package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UnlockType
import com.example.ui.dialogs.PinConfigDialog
import com.example.ui.dialogs.QuadrantTrainerDialog
import com.example.ui.dialogs.SecurityLogsDialog
import com.example.ui.dialogs.SymbolTrainerDialog
import com.example.ui.dialogs.TapTrainerDialog
import com.example.ui.screens.tabs.ArmTabScreen
import com.example.ui.screens.tabs.PracticeTabScreen
import com.example.ui.screens.tabs.SettingsTabScreen
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LockUiState
import com.example.ui.viewmodel.LockViewModel

enum class DashboardTab(val label: String, val icon: ImageVector, val tag: String) {
  ARM("Arm", Icons.Default.Shield, "nav_arm"),
  PRACTICE("Practice", Icons.Default.TouchApp, "nav_practice"),
  SETTINGS("Settings & Safety", Icons.Default.Settings, "nav_settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  uiState: LockUiState,
  viewModel: LockViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(DashboardTab.ARM) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("dashboard_screen"),
    containerColor = CyberDarkBg,
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF003845), RoundedCornerShape(10.dp))
                .border(1.dp, CyberCyanPrimary, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = CyberCyanPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "GhostLock",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = when (selectedTab) {
                  DashboardTab.ARM -> "Stealth Arming Console"
                  DashboardTab.PRACTICE -> "Interactive Training Sandbox"
                  DashboardTab.SETTINGS -> "Lockdown & Emergency Settings"
                },
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.setShowLogsDialog(true) },
            modifier = Modifier.testTag("action_logs")
          ) {
            Icon(Icons.Default.History, contentDescription = "Security Audit Logs", tint = CyberCyanPrimary)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkSurface)
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = CyberDarkSurface,
        contentColor = CyberCyanPrimary,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        DashboardTab.values().forEach { tab ->
          val isSelected = selectedTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTab = tab },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = if (isSelected) CyberCyanPrimary else TextSecondary
              )
            },
            label = {
              Text(
                text = tab.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CyberCyanPrimary else TextSecondary
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = CyberCyanPrimary,
              unselectedIconColor = TextSecondary,
              selectedTextColor = CyberCyanPrimary,
              unselectedTextColor = TextSecondary,
              indicatorColor = Color(0xFF003845)
            ),
            modifier = Modifier.testTag(tab.tag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        DashboardTab.ARM -> {
          ArmTabScreen(
            config = uiState.config,
            countdown = uiState.armCountdown,
            recentLogs = uiState.logs,
            onLockNow = { immediate -> viewModel.triggerLockNow(immediate = immediate) },
            onCancelCountdown = { viewModel.cancelCountdown() },
            onOpenTrainer = { viewModel.openTraining(it) },
            onViewLogs = { viewModel.setShowLogsDialog(true) }
          )
        }
        DashboardTab.PRACTICE -> {
          PracticeTabScreen(
            uiState = uiState,
            viewModel = viewModel,
            onNavigateToArm = { selectedTab = DashboardTab.ARM }
          )
        }
        DashboardTab.SETTINGS -> {
          SettingsTabScreen(
            config = uiState.config,
            viewModel = viewModel,
            onNavigateToArm = { selectedTab = DashboardTab.ARM }
          )
        }
      }
    }
  }

  // Dialogs
  if (uiState.activeTrainingType == UnlockType.SYMBOL) {
    SymbolTrainerDialog(
      onDismiss = { viewModel.closeTraining() },
      onSaveSymbol = { points ->
        viewModel.recordTrainingPoints(points)
        viewModel.saveTrainedSymbol()
      }
    )
  }

  if (uiState.activeTrainingType == UnlockType.TAP_COUNT) {
    TapTrainerDialog(
      initialTapCount = uiState.config.savedTapCount,
      onDismiss = { viewModel.closeTraining() },
      onSaveTapCount = { count ->
        viewModel.updateTempTapCount(count)
        viewModel.saveTrainedTapCount()
      }
    )
  }

  if (uiState.activeTrainingType == UnlockType.QUADRANT_COMBO) {
    QuadrantTrainerDialog(
      initialCombo = uiState.config.savedQuadrantCombo,
      onDismiss = { viewModel.closeTraining() },
      onSaveCombo = { combo ->
        combo.forEach { viewModel.appendTempQuadrant(it) }
        viewModel.saveTrainedQuadrantCombo()
      }
    )
  }

  if (uiState.showPinConfigDialog) {
    PinConfigDialog(
      currentPin = uiState.config.backupPin,
      onDismiss = { viewModel.setShowPinConfigDialog(false) },
      onSavePin = { viewModel.setBackupPin(it) }
    )
  }

  if (uiState.showLogsDialog) {
    SecurityLogsDialog(
      events = uiState.logs,
      onClearLogs = { viewModel.clearLogs() },
      onDismiss = { viewModel.setShowLogsDialog(false) }
    )
  }

  // Deceptive Dead Battery preview overlay if triggered
  DeadBatteryTamperOverlay(visible = uiState.isShowingDeadBattery)
}
