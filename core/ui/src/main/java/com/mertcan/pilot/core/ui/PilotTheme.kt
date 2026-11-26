package com.mertcan.pilot.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object PilotColors {
    val Background = Color(0xFF080D14)
    val Surface = Color(0xFF111B27)
    val SurfaceElevated = Color(0xFF182637)
    val Accent = Color(0xFF54C8FF)
    val AccentSoft = Color(0xFF173B50)
    val TextPrimary = Color(0xFFF3F7FB)
    val TextSecondary = Color(0xFF91A2B5)
    val Success = Color(0xFF54D6A0)
    val Warning = Color(0xFFFFC56A)
}

private val PilotDarkColorScheme = darkColorScheme(
    primary = PilotColors.Accent,
    onPrimary = Color(0xFF06131B),
    secondary = PilotColors.Success,
    background = PilotColors.Background,
    onBackground = PilotColors.TextPrimary,
    surface = PilotColors.Surface,
    onSurface = PilotColors.TextPrimary,
    surfaceVariant = PilotColors.SurfaceElevated,
    onSurfaceVariant = PilotColors.TextSecondary
)

@Composable
fun PilotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PilotDarkColorScheme,
        content = content
    )
}
