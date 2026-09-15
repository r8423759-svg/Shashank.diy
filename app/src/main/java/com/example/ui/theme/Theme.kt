package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BoltDarkColorScheme = darkColorScheme(
    primary = BoltElectricViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFDDD6FE),
    secondary = BoltElectricCyan,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = BoltEmerald,
    onTertiary = Color.White,
    background = BoltDarkBg,
    onBackground = BoltTextPrimary,
    surface = BoltDarkSurface,
    onSurface = BoltTextPrimary,
    surfaceVariant = BoltDarkElevated,
    onSurfaceVariant = BoltTextSecondary,
    outline = BoltDarkBorder,
    outlineVariant = BoltDarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Always enforce Bolt's intentional dark developer theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BoltDarkColorScheme,
        typography = Typography,
        content = content
    )
}
