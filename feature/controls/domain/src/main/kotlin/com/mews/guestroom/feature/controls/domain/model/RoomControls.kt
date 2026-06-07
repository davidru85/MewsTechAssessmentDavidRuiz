package com.mews.guestroom.feature.controls.domain.model

/**
 * Snapshot of everything a guest can see and control in their room.
 *
 * Pure domain model — no Android, framework, or data-source concerns. The mock
 * and (future) live data sources both produce this same shape.
 */
data class RoomControls(
    val climate: Climate,
    val lights: List<LightControl>,
    val blinds: BlindPosition,
    val activeScene: EnergyScene?,
)

/** Climate state: where the room is now vs. where the guest asked it to be. */
data class Climate(
    val currentCelsius: Double,
    val targetCelsius: Int,
    val mode: ClimateMode,
)

enum class ClimateMode { COOL, HEAT, AUTO, OFF }

/** A single controllable light. [isFaulty] flags a device known to be unreachable. */
data class LightControl(
    val id: String,
    val name: String,
    val isOn: Boolean,
    val isFaulty: Boolean = false,
)

enum class BlindPosition { OPEN, HALF, CLOSED }
