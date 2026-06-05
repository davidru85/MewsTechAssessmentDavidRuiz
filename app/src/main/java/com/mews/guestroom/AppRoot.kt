package com.mews.guestroom

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mews.guestroom.feature.controls.presentation.navigation.CONTROLS_ROUTE
import com.mews.guestroom.feature.controls.presentation.navigation.controlsGraph

/**
 * Application root: owns the navigation host and composes feature entry points.
 * The Controls dashboard is the prototype's start destination.
 */
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = CONTROLS_ROUTE) {
        controlsGraph()
    }
}
