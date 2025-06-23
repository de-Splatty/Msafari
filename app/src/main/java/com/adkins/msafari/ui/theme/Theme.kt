package com.adkins.msafari.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define custom colors
val Black = Color(0xFF000000)
val Green = Color(0xFF00FF00)
val White = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    background = Black,
    surface = Black,
    onPrimary = Black,
    onBackground = White,
    onSurface = White
)

@Composable
fun MsafariTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
