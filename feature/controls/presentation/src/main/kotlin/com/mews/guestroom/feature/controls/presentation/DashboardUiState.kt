package com.mews.guestroom.feature.controls.presentation

import com.mews.guestroom.feature.controls.domain.model.RoomControls

/**
 * What the dashboard renders. A hotel room always exposes controls (climate,
 * blinds, lights), so there is no "empty" state — only loading, then content.
 */
sealed interface DashboardUiState {
    /** Initial state before the first room snapshot arrives. */
    data object Loading : DashboardUiState

    /** Room state is available; [isActionInProgress] reflects an in-flight command. */
    data class Content(
        val controls: RoomControls,
        val isActionInProgress: Boolean,
    ) : DashboardUiState
}
