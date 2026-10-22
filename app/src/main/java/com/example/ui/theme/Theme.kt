package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GhostLockColorScheme = darkColorScheme(
  primary = CyberCyanPrimary,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF003845),
  onPrimaryContainer = CyberCyanPrimary,
  secondary = CyberCyanSecondary,
  onSecondary = Color.Black,
  tertiary = CyberAmber,
  onTertiary = Color.Black,
  background = CyberDarkBg,
  onBackground = TextPrimary,
  surface = CyberDarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberDarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = CyberOutline,
  error = CyberRed,
  onError = Color.Black
)

@Composable
fun GhostLockTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = GhostLockColorScheme,
    typography = Typography,
    content = content
  )
}
