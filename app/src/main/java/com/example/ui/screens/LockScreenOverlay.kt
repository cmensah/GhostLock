package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntSize
import com.example.data.model.FailsafeCorner
import com.example.data.model.PointF
import com.example.data.model.UnlockType
import com.example.ui.dialogs.EmergencyPinDialog
import com.example.ui.theme.StealthOledBlack
import com.example.ui.viewmodel.LockUiState
import com.example.ui.viewmodel.LockViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LockScreenOverlay(
  uiState: LockUiState,
  viewModel: LockViewModel,
  modifier: Modifier = Modifier
) {
  var viewSize by remember { mutableStateOf(IntSize.Zero) }
  val strokePoints = remember { mutableStateListOf<PointF>() }
  val scope = rememberCoroutineScope()
  var cornerHoldJob by remember { mutableStateOf<Job?>(null) }
  var isDraggingGesture by remember { mutableStateOf(false) }

  // Cancel hold job when leaving
  DisposableEffect(Unit) {
    onDispose {
      cornerHoldJob?.cancel()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(StealthOledBlack)
      .testTag("stealth_black_lock_screen")
      .onSizeChanged { viewSize = it }
      .pointerInput(uiState.config.activeUnlockType, uiState.config.failsafeCorner, viewSize) {
        awaitPointerEventScope {
          while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull() ?: continue
            val pos = change.position

            when (event.type) {
              PointerEventType.Press -> {
                change.consume()

                // 1. Check if touch is inside the secret failsafe corner
                val isCorner = isTouchInFailsafeCorner(
                  pos = pos,
                  size = viewSize,
                  corner = uiState.config.failsafeCorner
                )

                if (isCorner) {
                  cornerHoldJob?.cancel()
                  cornerHoldJob = scope.launch {
                    delay(3000) // 3-second sustained press triggers emergency PIN
                    viewModel.onEmergencyCornerLongPressed()
                  }
                }

                // 2. Process mode-specific down event
                when (uiState.config.activeUnlockType) {
                  UnlockType.SYMBOL -> {
                    strokePoints.clear()
                    strokePoints.add(PointF(pos.x, pos.y))
                    isDraggingGesture = true
                  }
                  UnlockType.TAP_COUNT -> {
                    // Tap will be processed on release or here
                  }
                  UnlockType.QUADRANT_COMBO -> {
                    if (viewSize.width > 0 && viewSize.height > 0) {
                      val q = determineQuadrant(pos, viewSize)
                      viewModel.onQuadrantTouched(q)
                    }
                  }
                }
              }

              PointerEventType.Move -> {
                change.consume()
                // If moved too far from corner, cancel corner hold
                val isCorner = isTouchInFailsafeCorner(
                  pos = pos,
                  size = viewSize,
                  corner = uiState.config.failsafeCorner
                )
                if (!isCorner) {
                  cornerHoldJob?.cancel()
                }

                if (isDraggingGesture && uiState.config.activeUnlockType == UnlockType.SYMBOL) {
                  strokePoints.add(PointF(pos.x, pos.y))
                }
              }

              PointerEventType.Release -> {
                change.consume()
                cornerHoldJob?.cancel()

                when (uiState.config.activeUnlockType) {
                  UnlockType.SYMBOL -> {
                    if (isDraggingGesture && strokePoints.size >= 4) {
                      viewModel.onGestureCompleted(strokePoints.toList())
                    }
                    strokePoints.clear()
                    isDraggingGesture = false
                  }
                  UnlockType.TAP_COUNT -> {
                    viewModel.onTapDetected()
                  }
                  UnlockType.QUADRANT_COMBO -> {
                    // Handled on press
                  }
                }
              }
            }
          }
        }
      }
  ) {
    // If user triggered Emergency PIN via corner long-press, show override dialog
    if (uiState.isEmergencyPinOpen) {
      EmergencyPinDialog(
        enteredPin = uiState.enteredEmergencyPin,
        targetPinLength = uiState.config.backupPin.length,
        errorMessage = uiState.pinErrorMessage,
        onDigitPress = { viewModel.appendEmergencyPinDigit(it) },
        onBackspace = { viewModel.deleteEmergencyPinDigit() },
        onDismiss = { viewModel.dismissEmergencyPin() }
      )
    }

    // Deceptive Dead Battery flash on failed attempts or button presses
    DeadBatteryTamperOverlay(visible = uiState.isShowingDeadBattery)
  }
}

private fun isTouchInFailsafeCorner(
  pos: Offset,
  size: IntSize,
  corner: FailsafeCorner
): Boolean {
  if (size.width <= 0 || size.height <= 0) return false
  val threshold = 180f // Corner touch boundary in px

  return when (corner) {
    FailsafeCorner.TOP_LEFT -> pos.x < threshold && pos.y < threshold
    FailsafeCorner.TOP_RIGHT -> pos.x > (size.width - threshold) && pos.y < threshold
    FailsafeCorner.BOTTOM_LEFT -> pos.x < threshold && pos.y > (size.height - threshold)
    FailsafeCorner.BOTTOM_RIGHT -> pos.x > (size.width - threshold) && pos.y > (size.height - threshold)
  }
}

private fun determineQuadrant(pos: Offset, size: IntSize): Int {
  val midX = size.width / 2f
  val midY = size.height / 2f

  return when {
    pos.x < midX && pos.y < midY -> 1 // Top-Left
    pos.x >= midX && pos.y < midY -> 2 // Top-Right
    pos.x < midX && pos.y >= midY -> 3 // Bottom-Left
    else -> 4 // Bottom-Right
  }
}
