package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PointF
import com.example.data.model.SecurityConfig
import com.example.data.model.UnlockType
import com.example.data.repository.SecurityRepository
import com.example.logic.GestureMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GhostLock", appName)
  }

  @Test
  fun `gesture matcher recognizes identical symbol`() {
    val defaultSymbol = SecurityConfig.defaultSymbolPoints()
    val score = GestureMatcher.calculateMatchScore(defaultSymbol, defaultSymbol)
    assertTrue("Self match score should be >= 0.85, got $score", score >= 0.85f)
  }

  @Test
  fun `security repository saves and retrieves config`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = SecurityRepository(context)

    val customConfig = SecurityConfig(
      activeUnlockType = UnlockType.TAP_COUNT,
      savedTapCount = 5,
      backupPin = "9876",
      selectedLanguageTag = "fr"
    )
    repo.updateConfig(customConfig)

    val loaded = repo.configFlow.value
    assertEquals(UnlockType.TAP_COUNT, loaded.activeUnlockType)
    assertEquals(5, loaded.savedTapCount)
    assertEquals("9876", loaded.backupPin)
    assertEquals("fr", loaded.selectedLanguageTag)
    assertTrue(loaded.showDeadBatteryOnTamper)
  }

  @Test
  fun `french locale resolves localized arm label`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val config = android.content.res.Configuration(context.resources.configuration)
    config.setLocale(java.util.Locale.FRENCH)
    val localizedContext = context.createConfigurationContext(config)
    val armLabel = localizedContext.getString(R.string.nav_arm)
    assertEquals("Armer", armLabel)
  }
}
