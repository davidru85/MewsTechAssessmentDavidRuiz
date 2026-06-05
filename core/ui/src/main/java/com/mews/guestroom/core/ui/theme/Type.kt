package com.mews.guestroom.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.mews.guestroom.core.ui.R

/**
 * Manrope is shipped as a single variable font ([R.font.manrope_variable]); each logical weight is
 * a [FontVariation] instance over the same file. Variable fonts require API 26+, which matches the
 * app's minSdk. See DESIGN.md → Typography.
 */
@OptIn(ExperimentalTextApi::class)
private fun manrope(weight: Int): Font =
    Font(
        resId = R.font.manrope_variable,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

val Manrope: FontFamily =
    FontFamily(
        manrope(500), // Medium  — body & labels
        manrope(600), // SemiBold — subheaders
        manrope(700), // Bold     — headlines
    )

/**
 * Maps the DESIGN.md type ramp onto Material 3 slots, all in Manrope:
 * - Headlines/Display: **Bold**, tight tracking (-0.02em).
 * - Titles (subheaders): **SemiBold**, open tracking (+0.01em).
 * - Body & labels: **Medium**, default tracking for legibility.
 */
private fun TextStyle.brand(weight: FontWeight, tracking: TextUnit = letterSpacing): TextStyle =
    copy(fontFamily = Manrope, fontWeight = weight, letterSpacing = tracking)

val GuestRoomTypography: Typography =
    Typography().run {
        val bold = FontWeight.Bold
        val semi = FontWeight.SemiBold
        val medium = FontWeight.Medium
        val headline = (-0.02).em
        val subheader = 0.01.em
        copy(
            displayLarge = displayLarge.brand(bold, headline),
            displayMedium = displayMedium.brand(bold, headline),
            displaySmall = displaySmall.brand(bold, headline),
            headlineLarge = headlineLarge.brand(bold, headline),
            headlineMedium = headlineMedium.brand(bold, headline),
            headlineSmall = headlineSmall.brand(bold, headline),
            titleLarge = titleLarge.brand(semi, subheader),
            titleMedium = titleMedium.brand(semi, subheader),
            titleSmall = titleSmall.brand(semi, subheader),
            bodyLarge = bodyLarge.brand(medium),
            bodyMedium = bodyMedium.brand(medium),
            bodySmall = bodySmall.brand(medium),
            labelLarge = labelLarge.brand(medium),
            labelMedium = labelMedium.brand(medium),
            labelSmall = labelSmall.brand(medium),
        )
    }
