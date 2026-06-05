package com.mews.guestroom.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF006A6A),
        secondary = Color(0xFF6F5B00),
        tertiary = Color(0xFF8B4A61),
        background = Color(0xFFFAFDFC),
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFF54D7D4),
        secondary = Color(0xFFE4C74C),
        tertiary = Color(0xFFFFB1C8),
        background = Color(0xFF101414),
    )

@Composable
fun GuestRoomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
