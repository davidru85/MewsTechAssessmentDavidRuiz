package com.mews.guestroom.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
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
val GuestRoomTypography: Typography =
    Typography().run {
        val headline = (-0.02).em
        val subheader = 0.01.em
        copy(
            displayLarge = displayLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, letterSpacing = headline),
            displayMedium = displayMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, letterSpacing = headline),
            displaySmall = displaySmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, letterSpacing = headline),
            headlineLarge = headlineLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, letterSpacing = headline),
            headlineMedium = headlineMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, letterSpacing = headline),
            headlineSmall = headlineSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, letterSpacing = headline),
            titleLarge = titleLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, letterSpacing = subheader),
            titleMedium = titleMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, letterSpacing = subheader),
            titleSmall = titleSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, letterSpacing = subheader),
            bodyLarge = bodyLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            bodyMedium = bodyMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            bodySmall = bodySmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            labelLarge = labelLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            labelMedium = labelMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            labelSmall = labelSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
        )
    }
