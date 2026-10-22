package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EventType
import com.example.data.model.FailsafeCorner
import com.example.data.model.LockState
import com.example.data.model.PointF
import com.example.data.model.SecurityConfig
import com.example.data.model.SecurityEvent
import com.example.data.model.UnlockType
import com.example.data.repository.SecurityRepository
import com.example.logic.GestureMatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LockUiState(
  val config: SecurityConfig = SecurityConfig(),
  val lockState: LockState = LockState.DASHBOARD,
  val isArmed: Boolean = false,
  val armCountdown: Int? = null,
  val isEmergencyPinOpen: Boolean = false,
  val enteredEmergencyPin: String = "",
  val pinErrorMessage: String? = null,
  val testModeActive: Boolean = false,
  val testFeedback: String? = null,
  val testFeedbackSuccess: Boolean = false,
  val logs: List<SecurityEvent> = emptyList(),
  val activeTrainingType: UnlockType? = null,
  val showLogsDialog: Boolean = false,
  val showPinConfigDialog: Boolean = false,
  val tempRecordedPoints: List<PointF> = emptyList(),
  val tempQuadrantList: List<Int> = emptyList(),
  val tempTapCount: Int = 4,
  val isShowingDeadBattery: Boolean = false,
  val failedAttempts: Int = 0
)

class LockViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = SecurityRepository(application.applicationContext)
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private val _uiState = MutableStateFlow(LockUiState())
  val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

  private var countdownJob: Job? = null
  private var deadBatteryJob: Job? = null
  private var tapAccumulator = mutableListOf<Long>()
  private var quadrantAccumulator = mutableListOf<Int>()
  private var lastTapTimestamp = 0L

  init {
    viewModelScope.launch {
      repository.configFlow.collect { config ->
        _uiState.update { it.copy(config = config) }
      }
    }
    viewModelScope.launch {
      repository.eventsFlow.collect { events ->
        _uiState.update { it.copy(logs = events) }
      }
    }
  }

  fun toggleArmed() {
    val newArmed = !_uiState.value.isArmed
    _uiState.update { it.copy(isArmed = newArmed) }
    if (newArmed) {
      repository.logEvent(EventType.ARMED, "GhostLock armed by user")
    } else {
      repository.logEvent(EventType.DISARMED, "GhostLock disarmed")
    }
  }

  fun triggerLockNow(immediate: Boolean = false) {
    countdownJob?.cancel()
    val delaySec = if (immediate) 0 else _uiState.value.config.armDelaySeconds

    if (delaySec <= 0) {
      startLockTransition()
    } else {
      countdownJob = viewModelScope.launch {
        for (sec in delaySec downTo 1) {
          _uiState.update { it.copy(armCountdown = sec) }
          delay(1000)
        }
        _uiState.update { it.copy(armCountdown = null) }
        startLockTransition()
      }
    }
  }

  fun cancelCountdown() {
    countdownJob?.cancel()
    countdownJob = null
    _uiState.update { it.copy(armCountdown = null) }
  }

  private fun startLockTransition() {
    tapAccumulator.clear()
    quadrantAccumulator.clear()
    if (_uiState.value.config.enableFakeShutdown) {
      _uiState.update { it.copy(lockState = LockState.FAKING_SHUTDOWN, isArmed = true) }
      viewModelScope.launch {
        delay(2500) // Fake shutdown duration
        _uiState.update { it.copy(lockState = LockState.LOCKED) }
      }
    } else {
      _uiState.update { it.copy(lockState = LockState.LOCKED, isArmed = true) }
    }
  }

  // --- Lock Screen Touch Handlers ---

  fun onGestureCompleted(points: List<PointF>, isTestMode: Boolean = false) {
    if (points.size < 4) return
    val template = _uiState.value.config.savedSymbolPoints
    val score = GestureMatcher.calculateMatchScore(points, template)
    val isMatch = score >= 0.65f

    if (isTestMode) {
      val percent = (score * 100).toInt()
      _uiState.update {
        it.copy(
          testFeedback = if (isMatch) "Match confirmed! ($percent%)" else "Symbol mismatch ($percent%). Try again.",
          testFeedbackSuccess = isMatch
        )
      }
      if (isMatch) triggerHapticFeedback()
      return
    }

    if (isMatch) {
      handleUnlockSuccess(EventType.UNLOCKED_SYMBOL, "Matched secret symbol (${(score * 100).toInt()}%)")
    } else {
      handleFailedAttempt("Drawn symbol mismatch (${(score * 100).toInt()}%)")
    }
  }

  fun onTapDetected(isTestMode: Boolean = false) {
    val now = System.currentTimeMillis()
    if (now - lastTapTimestamp > 1500) {
      tapAccumulator.clear()
    }
    lastTapTimestamp = now
    tapAccumulator.add(now)

    val targetCount = _uiState.value.config.savedTapCount

    if (isTestMode) {
      val current = tapAccumulator.size
      if (current == targetCount) {
        _uiState.update {
          it.copy(
            testFeedback = "Tap sequence matched! ($current/$targetCount taps)",
            testFeedbackSuccess = true
          )
        }
        triggerHapticFeedback()
        tapAccumulator.clear()
      } else if (current > targetCount) {
        _uiState.update {
          it.copy(
            testFeedback = "Too many taps ($current). Target is $targetCount.",
            testFeedbackSuccess = false
          )
        }
        tapAccumulator.clear()
      } else {
        _uiState.update {
          it.copy(
            testFeedback = "Taps registered: $current / $targetCount",
            testFeedbackSuccess = false
          )
        }
      }
      return
    }

    if (tapAccumulator.size == targetCount) {
      handleUnlockSuccess(EventType.UNLOCKED_TAPS, "Completed $targetCount-tap sequence")
      tapAccumulator.clear()
    } else if (tapAccumulator.size > targetCount + 2) {
      handleFailedAttempt("Tap sequence exceeded expected count")
      tapAccumulator.clear()
    }
  }

  fun onQuadrantTouched(quadrant: Int, isTestMode: Boolean = false) {
    quadrantAccumulator.add(quadrant)
    val targetCombo = _uiState.value.config.savedQuadrantCombo

    // Check prefix
    val currentSub = quadrantAccumulator.toList()
    val isPrefix = targetCombo.take(currentSub.size) == currentSub

    if (!isPrefix) {
      // Wrong quadrant sequence
      if (isTestMode) {
        _uiState.update {
          it.copy(
            testFeedback = "Incorrect quadrant sequence. Resetting.",
            testFeedbackSuccess = false
          )
        }
      } else {
        handleFailedAttempt("Incorrect quadrant sequence")
      }
      quadrantAccumulator.clear()
      return
    }

    if (currentSub.size == targetCombo.size) {
      // Complete match!
      if (isTestMode) {
        _uiState.update {
          it.copy(
            testFeedback = "Quadrant pattern matched: ${targetCombo.joinToString(" \u2192 ")}",
            testFeedbackSuccess = true
          )
        }
        triggerHapticFeedback()
      } else {
        handleUnlockSuccess(EventType.UNLOCKED_QUADRANTS, "Matched quadrant pattern")
      }
      quadrantAccumulator.clear()
    } else {
      if (isTestMode) {
        _uiState.update {
          it.copy(
            testFeedback = "Quadrant ${currentSub.size}/${targetCombo.size}: Q$quadrant registered",
            testFeedbackSuccess = false
          )
        }
      }
    }
  }

  fun onEmergencyCornerLongPressed() {
    _uiState.update {
      it.copy(
        isEmergencyPinOpen = true,
        enteredEmergencyPin = "",
        pinErrorMessage = null
      )
    }
    triggerHapticFeedback()
  }

  fun appendEmergencyPinDigit(digit: Char) {
    val current = _uiState.value.enteredEmergencyPin
    if (current.length < 8) {
      val updated = current + digit
      _uiState.update { it.copy(enteredEmergencyPin = updated, pinErrorMessage = null) }
      if (updated.length == _uiState.value.config.backupPin.length) {
        submitEmergencyPin()
      }
    }
  }

  fun deleteEmergencyPinDigit() {
    val current = _uiState.value.enteredEmergencyPin
    if (current.isNotEmpty()) {
      _uiState.update { it.copy(enteredEmergencyPin = current.dropLast(1), pinErrorMessage = null) }
    }
  }

  fun dismissEmergencyPin() {
    _uiState.update {
      it.copy(
        isEmergencyPinOpen = false,
        enteredEmergencyPin = "",
        pinErrorMessage = null
      )
    }
  }

  fun submitEmergencyPin() {
    val entered = _uiState.value.enteredEmergencyPin
    val actual = _uiState.value.config.backupPin
    if (entered == actual) {
      _uiState.update { it.copy(isEmergencyPinOpen = false, enteredEmergencyPin = "") }
      handleUnlockSuccess(EventType.UNLOCKED_FAILSAFE_PIN, "Unlocked via Emergency Corner PIN")
    } else {
      _uiState.update {
        it.copy(
          enteredEmergencyPin = "",
          pinErrorMessage = "Incorrect PIN. Stealth lock remains active."
        )
      }
      handleFailedAttempt("Failed emergency PIN attempt")
    }
  }

  private fun handleUnlockSuccess(eventType: EventType, details: String) {
    triggerHapticFeedback()
    deadBatteryJob?.cancel()
    repository.logEvent(eventType, details)
    _uiState.update {
      it.copy(
        lockState = LockState.DASHBOARD,
        isEmergencyPinOpen = false,
        enteredEmergencyPin = "",
        testFeedback = null,
        isShowingDeadBattery = false,
        failedAttempts = 0
      )
    }
  }

  private fun handleFailedAttempt(details: String) {
    val currentAttempts = _uiState.value.failedAttempts + 1
    _uiState.update { it.copy(failedAttempts = currentAttempts) }
    repository.logEvent(EventType.FAILED_ATTEMPT, "$details (Attempt #$currentAttempts)")
    triggerDeadBatteryFlash()
  }

  fun onHardwareButtonTamper(buttonName: String) {
    val currentAttempts = _uiState.value.failedAttempts + 1
    _uiState.update { it.copy(failedAttempts = currentAttempts) }
    repository.logEvent(EventType.FAILED_ATTEMPT, "Hardware button ($buttonName) pressed while locked")
    triggerDeadBatteryFlash()
  }

  fun triggerDeadBatteryFlash() {
    if (!_uiState.value.config.showDeadBatteryOnTamper) return
    deadBatteryJob?.cancel()
    deadBatteryJob = viewModelScope.launch {
      _uiState.update { it.copy(isShowingDeadBattery = true) }
      delay(1800) // Display 0% dead battery for 1.8 seconds
      _uiState.update { it.copy(isShowingDeadBattery = false) }
    }
  }

  private fun triggerHapticFeedback() {
    if (!_uiState.value.config.vibrateOnSuccess) return
    runCatching {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(80)
      }
    }
  }

  // --- Configuration & Training ---

  fun setUnlockType(type: UnlockType) {
    val updated = _uiState.value.config.copy(activeUnlockType = type)
    repository.updateConfig(updated)
  }

  fun setFailsafeCorner(corner: FailsafeCorner) {
    val updated = _uiState.value.config.copy(failsafeCorner = corner)
    repository.updateConfig(updated)
  }

  fun toggleFakeShutdown(enabled: Boolean) {
    val updated = _uiState.value.config.copy(enableFakeShutdown = enabled)
    repository.updateConfig(updated)
  }

  fun toggleVibrateOnSuccess(enabled: Boolean) {
    val updated = _uiState.value.config.copy(vibrateOnSuccess = enabled)
    repository.updateConfig(updated)
  }

  fun toggleDimBrightness(enabled: Boolean) {
    val updated = _uiState.value.config.copy(dimBrightnessToMin = enabled)
    repository.updateConfig(updated)
  }

  fun toggleEnableLockTask(enabled: Boolean) {
    val updated = _uiState.value.config.copy(enableLockTask = enabled)
    repository.updateConfig(updated)
  }

  fun toggleAutoReopenOnHome(enabled: Boolean) {
    val updated = _uiState.value.config.copy(autoReopenOnHome = enabled)
    repository.updateConfig(updated)
  }

  fun toggleShowDeadBatteryOnTamper(enabled: Boolean) {
    val updated = _uiState.value.config.copy(showDeadBatteryOnTamper = enabled)
    repository.updateConfig(updated)
  }

  fun setArmDelaySeconds(seconds: Int) {
    val updated = _uiState.value.config.copy(armDelaySeconds = seconds)
    repository.updateConfig(updated)
  }

  fun setLanguage(languageTag: String) {
    val updated = _uiState.value.config.copy(selectedLanguageTag = languageTag)
    repository.updateConfig(updated)
  }

  fun setBackupPin(pin: String) {
    if (pin.length in 4..8) {
      val updated = _uiState.value.config.copy(backupPin = pin)
      repository.updateConfig(updated)
      _uiState.update { it.copy(showPinConfigDialog = false) }
    }
  }

  fun openTraining(type: UnlockType) {
    when (type) {
      UnlockType.SYMBOL -> _uiState.update {
        it.copy(activeTrainingType = type, tempRecordedPoints = emptyList())
      }
      UnlockType.TAP_COUNT -> _uiState.update {
        it.copy(activeTrainingType = type, tempTapCount = it.config.savedTapCount)
      }
      UnlockType.QUADRANT_COMBO -> _uiState.update {
        it.copy(activeTrainingType = type, tempQuadrantList = emptyList())
      }
    }
  }

  fun closeTraining() {
    _uiState.update {
      it.copy(
        activeTrainingType = null,
        tempRecordedPoints = emptyList(),
        tempQuadrantList = emptyList()
      )
    }
  }

  fun recordTrainingPoints(points: List<PointF>) {
    _uiState.update { it.copy(tempRecordedPoints = points) }
  }

  fun saveTrainedSymbol() {
    val points = _uiState.value.tempRecordedPoints
    if (points.size >= 4) {
      val normalized = GestureMatcher.normalize(points)
      val updated = _uiState.value.config.copy(
        savedSymbolPoints = normalized,
        activeUnlockType = UnlockType.SYMBOL
      )
      repository.updateConfig(updated)
      closeTraining()
    }
  }

  fun updateTempTapCount(count: Int) {
    _uiState.update { it.copy(tempTapCount = count.coerceIn(2, 10)) }
  }

  fun saveTrainedTapCount() {
    val count = _uiState.value.tempTapCount
    val updated = _uiState.value.config.copy(
      savedTapCount = count,
      activeUnlockType = UnlockType.TAP_COUNT
    )
    repository.updateConfig(updated)
    closeTraining()
  }

  fun appendTempQuadrant(q: Int) {
    val current = _uiState.value.tempQuadrantList.toMutableList()
    if (current.size < 8) {
      current.add(q)
      _uiState.update { it.copy(tempQuadrantList = current) }
    }
  }

  fun clearTempQuadrants() {
    _uiState.update { it.copy(tempQuadrantList = emptyList()) }
  }

  fun saveTrainedQuadrantCombo() {
    val combo = _uiState.value.tempQuadrantList
    if (combo.size >= 2) {
      val updated = _uiState.value.config.copy(
        savedQuadrantCombo = combo,
        activeUnlockType = UnlockType.QUADRANT_COMBO
      )
      repository.updateConfig(updated)
      closeTraining()
    }
  }

  fun setTestMode(active: Boolean) {
    tapAccumulator.clear()
    quadrantAccumulator.clear()
    _uiState.update {
      it.copy(
        testModeActive = active,
        testFeedback = if (active) "Sandbox Active. Test your ${it.config.activeUnlockType.displayName} below." else null,
        testFeedbackSuccess = false
      )
    }
  }

  fun setShowLogsDialog(show: Boolean) {
    _uiState.update { it.copy(showLogsDialog = show) }
  }

  fun setShowPinConfigDialog(show: Boolean) {
    _uiState.update { it.copy(showPinConfigDialog = show) }
  }

  fun clearLogs() {
    repository.clearLogs()
  }
}
