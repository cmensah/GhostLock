package com.example

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LockState
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FakeShutdownOverlay
import com.example.ui.screens.LockScreenOverlay
import com.example.ui.theme.GhostLockTheme
import com.example.ui.viewmodel.LockViewModel

import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class MainActivity : ComponentActivity() {
  private val viewModel: LockViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      val currentContext = LocalContext.current
      val currentConfig = LocalConfiguration.current

      val localizedContext = remember(uiState.config.selectedLanguageTag) {
        if (uiState.config.selectedLanguageTag.isNotEmpty()) {
          val locale = Locale.forLanguageTag(uiState.config.selectedLanguageTag)
          Locale.setDefault(locale)
          val config = Configuration(currentConfig).apply {
            setLocale(locale)
          }
          currentContext.createConfigurationContext(config)
        } else {
          currentContext
        }
      }

      val localizedConfig = remember(uiState.config.selectedLanguageTag) {
        if (uiState.config.selectedLanguageTag.isNotEmpty()) {
          Configuration(currentConfig).apply {
            setLocale(Locale.forLanguageTag(uiState.config.selectedLanguageTag))
          }
        } else {
          currentConfig
        }
      }

      CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfig
      ) {
        GhostLockTheme {
          // Sync system window flags, brightness, lock task mode, and immersive bars based on LockState
          LaunchedEffect(uiState.lockState, uiState.config.dimBrightnessToMin, uiState.config.enableLockTask) {
            applySystemLockBehavior(
              lockState = uiState.lockState,
              dimBrightness = uiState.config.dimBrightnessToMin,
              enableLockTask = uiState.config.enableLockTask
            )
          }

          Surface(modifier = Modifier.fillMaxSize()) {
          when (uiState.lockState) {
            LockState.DASHBOARD -> {
              DashboardScreen(
                uiState = uiState,
                viewModel = viewModel
              )
            }
            LockState.FAKING_SHUTDOWN -> {
              BackHandler {
                // Back button is disabled during stealth shutdown
              }
              FakeShutdownOverlay()
            }
            LockState.LOCKED -> {
              BackHandler {
                // Back button is swallowed during lock mode
              }
              LockScreenOverlay(
                uiState = uiState,
                viewModel = viewModel
              )
            }
          }
          }
        }
      }
    }
  }

  private fun applySystemLockBehavior(
    lockState: LockState,
    dimBrightness: Boolean,
    enableLockTask: Boolean
  ) {
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

    if (lockState != LockState.DASHBOARD) {
      // Enter Full Immersive Stealth Mode
      windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

      // Keep screen awake so touches are caught without waking screen physically
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

      // Dim screen brightness to minimum OLED levels if enabled
      if (dimBrightness) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.005f
        window.attributes = layoutParams
      }

      // Workaround: Enter Lock Task Mode (Screen Pinning) to disable Home & Recents
      if (enableLockTask) {
        runCatching {
          startLockTask()
        }
      }
    } else {
      // Restore normal dashboard behavior
      windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
      window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

      val layoutParams = window.attributes
      layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
      window.attributes = layoutParams

      // Exit Lock Task Mode
      runCatching {
        stopLockTask()
      }
    }
  }

  /**
   * Called by Android when the user attempts to leave the app via Home button or gesture.
   * Workaround: Instantly brings the stealth lock screen back to the front!
   */
  override fun onUserLeaveHint() {
    super.onUserLeaveHint()
    val isLocked = viewModel.uiState.value.lockState != LockState.DASHBOARD
    val autoReopen = viewModel.uiState.value.config.autoReopenOnHome
    if (isLocked && autoReopen) {
      reassertLockForeground()
    }
  }

  /**
   * If window focus is lost while in stealth lock mode (e.g., Home pressed or quick-settings pulled),
   * instantly reclaim foreground focus so the device never shows the home screen.
   */
  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    val isLocked = viewModel.uiState.value.lockState != LockState.DASHBOARD
    val autoReopen = viewModel.uiState.value.config.autoReopenOnHome
    if (!hasFocus && isLocked && autoReopen) {
      reassertLockForeground()
    }
  }

  private fun reassertLockForeground() {
    val intent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    startActivity(intent)
  }

  /**
   * Intercept and suppress physical buttons (Volume Up, Volume Down, Volume Mute, Back)
   * while stealth lock is engaged so the phone does not display system volume sliders.
   */
  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    val isLocked = viewModel.uiState.value.lockState != LockState.DASHBOARD
    if (isLocked) {
      when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_UP -> {
          viewModel.onHardwareButtonTamper("Volume Up")
          return true
        }
        KeyEvent.KEYCODE_VOLUME_DOWN -> {
          viewModel.onHardwareButtonTamper("Volume Down")
          return true
        }
        KeyEvent.KEYCODE_VOLUME_MUTE -> {
          viewModel.onHardwareButtonTamper("Volume Mute")
          return true
        }
        KeyEvent.KEYCODE_BACK -> {
          viewModel.onHardwareButtonTamper("Back")
          return true
        }
      }
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    val isLocked = viewModel.uiState.value.lockState != LockState.DASHBOARD
    if (isLocked) {
      when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_UP,
        KeyEvent.KEYCODE_VOLUME_DOWN,
        KeyEvent.KEYCODE_VOLUME_MUTE,
        KeyEvent.KEYCODE_BACK -> {
          return true
        }
      }
    }
    return super.onKeyUp(keyCode, event)
  }
}
