package com.mews.guestroom.feature.controls.presentation

/** One-shot events the dashboard surfaces (e.g. a snackbar), never re-emitted on rotation. */
sealed interface DashboardEvent {
    data class ShowMessage(val message: String) : DashboardEvent
}
