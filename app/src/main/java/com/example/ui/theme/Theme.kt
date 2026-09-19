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

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B0B14),
    onPrimaryContainer = Color(0xFFFFD9DF),
    secondary = RedSecondary,
    onSecondary = Color.White,
    tertiary = RedTertiary,
    background = DarkBackground,
    onBackground = Color(0xFFEEEEF2),
    surface = DarkSurface,
    onSurface = Color(0xFFEEEEF2),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFAAAAAF),
    outline = DarkOutline,
    outlineVariant = Color(0xFF2E2E36)
)

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0E5),
    onPrimaryContainer = Color(0xFF40000C),
    secondary = RedSecondary,
    onSecondary = Color.White,
    tertiary = RedTertiary,
    background = LightBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = LightSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF49454E),
    outline = LightOutline,
    outlineVariant = Color(0xFFE2E2E8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to immersive dark streaming theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
