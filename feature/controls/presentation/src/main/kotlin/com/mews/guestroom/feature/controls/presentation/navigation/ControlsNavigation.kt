package com.mews.guestroom.feature.controls.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mews.guestroom.feature.controls.presentation.DashboardScreen

/** Route for the Controls dashboard, owned by this feature. */
const val CONTROLS_ROUTE = "controls"

/**
 * Feature navigation entry point. `:app` composes this into the root graph;
 * features never navigate directly into one another.
 */
fun NavGraphBuilder.controlsGraph() {
    composable(CONTROLS_ROUTE) { DashboardScreen() }
}
