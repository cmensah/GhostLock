package com.example.data.model

enum class UnlockType(val displayName: String, val description: String) {
  SYMBOL("Drawn Symbol", "Draw a secret continuous symbol or shape anywhere on the dark screen"),
  TAP_COUNT("Tap Sequence", "Tap the screen a secret number of times in rhythm"),
  QUADRANT_COMBO("Quadrant Pattern", "Tap secret screen quadrants in an exact sequence")
}

enum class FailsafeCorner(val displayName: String, val shortName: String) {
  TOP_LEFT("Top-Left Corner", "TL"),
  TOP_RIGHT("Top-Right Corner", "TR"),
  BOTTOM_LEFT("Bottom-Left Corner", "BL"),
  BOTTOM_RIGHT("Bottom-Right Corner", "BR")
}

enum class LockState {
  DASHBOARD,
  FAKING_SHUTDOWN,
  LOCKED
}

data class PointF(
  val x: Float,
  val y: Float
)

data class SecurityEvent(
  val id: Long = System.currentTimeMillis(),
  val timestamp: Long = System.currentTimeMillis(),
  val eventType: EventType,
  val details: String
)

enum class EventType {
  ARMED,
  UNLOCKED_SYMBOL,
  UNLOCKED_TAPS,
  UNLOCKED_QUADRANTS,
  UNLOCKED_FAILSAFE_PIN,
  FAILED_ATTEMPT,
  DISARMED
}

data class SecurityConfig(
  val activeUnlockType: UnlockType = UnlockType.SYMBOL,
  val savedSymbolPoints: List<PointF> = defaultSymbolPoints(),
  val savedTapCount: Int = 4,
  val savedQuadrantCombo: List<Int> = listOf(1, 2, 4, 3), // Quadrants 1..4
  val backupPin: String = "1234",
  val failsafeCorner: FailsafeCorner = FailsafeCorner.TOP_RIGHT,
  val enableFakeShutdown: Boolean = true,
  val vibrateOnSuccess: Boolean = true,
  val dimBrightnessToMin: Boolean = true,
  val armDelaySeconds: Int = 3,
  val enableLockTask: Boolean = true,
  val autoReopenOnHome: Boolean = true,
  val showDeadBatteryOnTamper: Boolean = true,
  val selectedLanguageTag: String = "" // Empty string = System Default
) {
  companion object {
    fun defaultSymbolPoints(): List<PointF> {
      // Default: A clean checkmark or 'Z' shape
      return listOf(
        PointF(0.2f, 0.2f),
        PointF(0.8f, 0.2f),
        PointF(0.2f, 0.8f),
        PointF(0.8f, 0.8f)
      )
    }
  }
}
