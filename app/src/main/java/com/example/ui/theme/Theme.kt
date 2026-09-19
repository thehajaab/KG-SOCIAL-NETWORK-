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
    primary = SkyBlue,
    onPrimary = DeepNavyDarker,
    primaryContainer = DeepNavyLight,
    onPrimaryContainer = SkyBlue,
    secondary = AccentPurple,
    onSecondary = PureWhite,
    tertiary = ElectricBlue,
    background = DeepNavyDarker,
    surface = DeepNavy,
    onBackground = PureWhite,
    onSurface = PureWhite,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ElectricBlue,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFE8F1FF),
    onPrimaryContainer = DeepNavy,
    secondary = AccentPurple,
    onSecondary = PureWhite,
    tertiary = SkyBlue,
    background = SoftGray,
    surface = PureWhite,
    onBackground = DarkText,
    onSurface = DarkText,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
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
