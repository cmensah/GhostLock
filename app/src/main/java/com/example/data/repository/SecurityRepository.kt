package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.EventType
import com.example.data.model.FailsafeCorner
import com.example.data.model.PointF
import com.example.data.model.SecurityConfig
import com.example.data.model.SecurityEvent
import com.example.data.model.UnlockType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class SecurityRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("ghostlock_prefs", Context.MODE_PRIVATE)

  private val _configFlow = MutableStateFlow(loadConfig())
  val configFlow: StateFlow<SecurityConfig> = _configFlow.asStateFlow()

  private val _eventsFlow = MutableStateFlow(loadEvents())
  val eventsFlow: StateFlow<List<SecurityEvent>> = _eventsFlow.asStateFlow()

  private fun loadConfig(): SecurityConfig {
    val unlockTypeName = prefs.getString("unlock_type", UnlockType.SYMBOL.name) ?: UnlockType.SYMBOL.name
    val unlockType = runCatching { UnlockType.valueOf(unlockTypeName) }.getOrDefault(UnlockType.SYMBOL)

    val cornerName = prefs.getString("failsafe_corner", FailsafeCorner.TOP_RIGHT.name) ?: FailsafeCorner.TOP_RIGHT.name
    val corner = runCatching { FailsafeCorner.valueOf(cornerName) }.getOrDefault(FailsafeCorner.TOP_RIGHT)

    val tapCount = prefs.getInt("tap_count", 4)
    val pin = prefs.getString("backup_pin", "1234") ?: "1234"
    val fakeShutdown = prefs.getBoolean("fake_shutdown", true)
    val vibrate = prefs.getBoolean("vibrate_on_success", true)
    val dimBrightness = prefs.getBoolean("dim_brightness", true)
    val armDelay = prefs.getInt("arm_delay", 3)

    // Load Quadrant Combo
    val comboStr = prefs.getString("quadrant_combo", "1,2,4,3") ?: "1,2,4,3"
    val combo = comboStr.split(",")
      .mapNotNull { it.trim().toIntOrNull() }
      .ifEmpty { listOf(1, 2, 4, 3) }

    // Load Symbol Points
    val pointsJson = prefs.getString("symbol_points", null)
    val symbolPoints = if (pointsJson != null) {
      parsePoints(pointsJson)
    } else {
      SecurityConfig.defaultSymbolPoints()
    }

    val enableLockTask = prefs.getBoolean("enable_lock_task", true)
    val autoReopen = prefs.getBoolean("auto_reopen_on_home", true)
    val showDeadBattery = prefs.getBoolean("show_dead_battery_on_tamper", true)

    return SecurityConfig(
      activeUnlockType = unlockType,
      savedSymbolPoints = symbolPoints,
      savedTapCount = tapCount,
      savedQuadrantCombo = combo,
      backupPin = pin,
      failsafeCorner = corner,
      enableFakeShutdown = fakeShutdown,
      vibrateOnSuccess = vibrate,
      dimBrightnessToMin = dimBrightness,
      armDelaySeconds = armDelay,
      enableLockTask = enableLockTask,
      autoReopenOnHome = autoReopen,
      showDeadBatteryOnTamper = showDeadBattery
    )
  }

  fun updateConfig(config: SecurityConfig) {
    _configFlow.value = config
    prefs.edit().apply {
      putString("unlock_type", config.activeUnlockType.name)
      putString("failsafe_corner", config.failsafeCorner.name)
      putInt("tap_count", config.savedTapCount)
      putString("backup_pin", config.backupPin)
      putBoolean("fake_shutdown", config.enableFakeShutdown)
      putBoolean("vibrate_on_success", config.vibrateOnSuccess)
      putBoolean("dim_brightness", config.dimBrightnessToMin)
      putInt("arm_delay", config.armDelaySeconds)
      putBoolean("enable_lock_task", config.enableLockTask)
      putBoolean("auto_reopen_on_home", config.autoReopenOnHome)
      putBoolean("show_dead_battery_on_tamper", config.showDeadBatteryOnTamper)
      putString("quadrant_combo", config.savedQuadrantCombo.joinToString(","))
      putString("symbol_points", serializePoints(config.savedSymbolPoints))
      apply()
    }
  }

  private fun parsePoints(json: String): List<PointF> {
    return runCatching {
      val array = JSONArray(json)
      val list = mutableListOf<PointF>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(PointF(obj.getDouble("x").toFloat(), obj.getDouble("y").toFloat()))
      }
      if (list.size >= 2) list else SecurityConfig.defaultSymbolPoints()
    }.getOrDefault(SecurityConfig.defaultSymbolPoints())
  }

  private fun serializePoints(points: List<PointF>): String {
    val array = JSONArray()
    for (pt in points) {
      val obj = JSONObject()
      obj.put("x", pt.x.toDouble())
      obj.put("y", pt.y.toDouble())
      array.put(obj)
    }
    return array.toString()
  }

  fun logEvent(type: EventType, details: String) {
    val newEvent = SecurityEvent(eventType = type, details = details)
    val current = _eventsFlow.value.toMutableList()
    current.add(0, newEvent)
    val trimmed = current.take(30)
    _eventsFlow.value = trimmed
    saveEvents(trimmed)
  }

  fun clearLogs() {
    _eventsFlow.value = emptyList()
    prefs.edit().remove("security_events").apply()
  }

  private fun saveEvents(events: List<SecurityEvent>) {
    val array = JSONArray()
    for (ev in events) {
      val obj = JSONObject()
      obj.put("id", ev.id)
      obj.put("timestamp", ev.timestamp)
      obj.put("type", ev.eventType.name)
      obj.put("details", ev.details)
      array.put(obj)
    }
    prefs.edit().putString("security_events", array.toString()).apply()
  }

  private fun loadEvents(): List<SecurityEvent> {
    val json = prefs.getString("security_events", null) ?: return emptyList()
    return runCatching {
      val array = JSONArray(json)
      val list = mutableListOf<SecurityEvent>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          SecurityEvent(
            id = obj.getLong("id"),
            timestamp = obj.getLong("timestamp"),
            eventType = EventType.valueOf(obj.getString("type")),
            details = obj.getString("details")
          )
        )
      }
      list
    }.getOrDefault(emptyList())
  }
}
