package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.StealthOledBlack

@Composable
fun DeadBatteryTamperOverlay(
  visible: Boolean,
  modifier: Modifier = Modifier
) {
  val redBlink = remember { Animatable(1f) }

  LaunchedEffect(visible) {
    if (visible) {
      redBlink.animateTo(
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 400, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    }
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(180)),
    exit = fadeOut(tween(350)),
    modifier = modifier.fillMaxSize()
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(StealthOledBlack)
        .testTag("dead_battery_tamper_overlay"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Battery Body + Tip
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          // Battery Chassis
          Box(
            modifier = Modifier
              .width(96.dp)
              .height(48.dp)
              .border(
                width = 3.dp,
                color = Color(0xFFDDDDDD),
                shape = RoundedCornerShape(8.dp)
              )
              .padding(4.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            // Depleted Red Bar (Empty Battery)
            Box(
              modifier = Modifier
                .width(14.dp)
                .height(34.dp)
                .alpha(redBlink.value)
                .background(Color(0xFFFF3333), RoundedCornerShape(3.dp))
            )

            // Warning Exclamation
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = stringResource(R.string.battery_depleted_header),
              tint = Color(0xFFFF3333),
              modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center)
                .alpha(redBlink.value)
            )
          }

          // Battery Positive Terminal Pin
          Box(
            modifier = Modifier
              .width(6.dp)
              .height(18.dp)
              .background(
                color = Color(0xFFDDDDDD),
                shape = RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp)
              )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = stringResource(R.string.battery_depleted_header),
          color = Color(0xFFFF3333),
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = stringResource(R.string.battery_connect_charger),
          color = Color(0xFF888888),
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal
        )
      }
    }
  }
}
