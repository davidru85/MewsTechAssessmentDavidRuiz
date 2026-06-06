package com.mews.guestroom.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// --- Color tokens (DESIGN.md → Color Palette). Light & dark are structurally identical;
// only the mapping changes (Design Principle 4: Perfect Symmetry). ---

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF005050),
        onPrimary = Color(0xFFFFFFFF),
        background = Color(0xFFF8FAF9), // Surface
        onBackground = Color(0xFF191C1C), // On-Surface
        surface = Color(0xFFF8FAF9), // Surface
        onSurface = Color(0xFF191C1C), // On-Surface
        surfaceContainer = Color(0xFFFFFFFF), // Surface-Container (card backgrounds)
        surfaceVariant = Color(0xFFFFFFFF),
        onSurfaceVariant = Color(0xFF404948), // On-Surface-Variant
        outlineVariant = Color(0xFFDAE5E3), // Outline-Variant
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFF54D7D4),
        onPrimary = Color(0xFF003737),
        background = Color(0xFF101414), // Surface
        onBackground = Color(0xFFE1E3E3), // On-Surface
        surface = Color(0xFF101414), // Surface
        onSurface = Color(0xFFE1E3E3), // On-Surface
        surfaceContainer = Color(0xFF1A1F1F), // Surface-Container (card backgrounds)
        surfaceVariant = Color(0xFF1A1F1F),
        onSurfaceVariant = Color(0xFFBFC8C7), // On-Surface-Variant
        outlineVariant = Color(0xFF3F4948), // Outline-Variant
    )

// DESIGN.md → Shapes & Spacing: 24dp section cards, 12dp internal buttons/chips, 8dp affordances.
private val GuestRoomShapes =
    Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(24.dp),
    )

@Composable
fun GuestRoomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = GuestRoomTypography,
        shapes = GuestRoomShapes,
        content = content,
    )
}
