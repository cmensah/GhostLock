package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StealthOledBlack

@Composable
fun FakeShutdownOverlay(
  modifier: Modifier = Modifier
) {
  val contentAlpha = remember { Animatable(1f) }

  LaunchedEffect(Unit) {
    // Hold visible for 1.8 seconds, then fade out over 0.7s into pure pitch black
    kotlinx.coroutines.delay(1800)
    contentAlpha.animateTo(
      targetValue = 0f,
      animationSpec = tween(durationMillis = 700, easing = LinearEasing)
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(StealthOledBlack)
      .testTag("fake_shutdown_overlay"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.alpha(contentAlpha.value),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .background(Color(0xFF1E232B), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.BatteryAlert,
          contentDescription = "Battery critically low",
          tint = Color(0xFFFF5252),
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      CircularProgressIndicator(
        modifier = Modifier.size(36.dp),
        color = Color(0xFFE0E0E0),
        strokeWidth = 3.dp
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Shutting down...",
        color = Color(0xFFF5F5F5),
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Battery critically low (1%)",
        color = Color(0xFF9E9E9E),
        fontSize = 13.sp
      )
    }
  }
}
