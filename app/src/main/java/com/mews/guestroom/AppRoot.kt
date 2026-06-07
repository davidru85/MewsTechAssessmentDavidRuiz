package com.mews.guestroom

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.RoomService
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mews.guestroom.core.ui.components.PlaceholderScreen
import com.mews.guestroom.feature.controls.presentation.navigation.CONTROLS_ROUTE
import com.mews.guestroom.feature.controls.presentation.navigation.controlsGraph

const val SERVICES_ROUTE = "services"
const val KEYS_ROUTE = "keys"

/** Top-level bottom-nav destinations (DESIGN.md §6). */
private data class TopLevelDestination(val route: String, val label: String, val icon: ImageVector)

private val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(CONTROLS_ROUTE, "Room", Icons.Filled.MeetingRoom),
    TopLevelDestination(SERVICES_ROUTE, "Services", Icons.Outlined.RoomService),
    TopLevelDestination(KEYS_ROUTE, "Keys", Icons.Outlined.VpnKey),
)

/**
 * Application root: an app-level shell owning the persistent bottom navigation and
 * the [NavHost]. The bottom bar survives across tabs and its selection follows the
 * current route. Features expose their own graphs; they never navigate into one
 * another directly. Services and Keys are lightweight placeholders for now.
 */
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { AppBottomNav(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = CONTROLS_ROUTE,
            modifier = Modifier.padding(padding),
        ) {
            controlsGraph()
            composable(SERVICES_ROUTE) {
                PlaceholderScreen(title = "Services", icon = Icons.Outlined.RoomService)
            }
            composable(KEYS_ROUTE) {
                PlaceholderScreen(title = "Keys", icon = Icons.Outlined.VpnKey)
            }
        }
    }
}

@Composable
private fun AppBottomNav(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TOP_LEVEL_DESTINATIONS.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            // Single-top + restore so re-selecting a tab doesn't stack copies.
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.label) },
                colors = itemColors,
            )
        }
    }
}
