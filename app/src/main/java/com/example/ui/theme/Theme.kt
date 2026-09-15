package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = WellnessGreenLight,
    onPrimary = Color.White,
    primaryContainer = WellnessGreenDark,
    onPrimaryContainer = WellnessGreenContainer,
    secondary = WellnessOrangeLight,
    onSecondary = Color.White,
    secondaryContainer = WellnessOrange,
    tertiary = WellnessLimeLight,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = WellnessGreen,
    onPrimary = Color.White,
    primaryContainer = WellnessGreenContainer,
    onPrimaryContainer = WellnessGreenDark,
    secondary = WellnessOrange,
    onSecondary = Color.White,
    secondaryContainer = WellnessOrangeContainer,
    tertiary = WellnessLime,
    background = WellnessCreamBg,
    surface = WellnessCardSurface,
    onBackground = WellnessTextPrimary,
    onSurface = WellnessTextPrimary,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
