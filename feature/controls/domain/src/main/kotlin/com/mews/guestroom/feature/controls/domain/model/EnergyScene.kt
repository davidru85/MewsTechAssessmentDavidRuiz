package com.mews.guestroom.feature.controls.domain.model

/**
 * One-tap energy scenes that orchestrate several devices at once.
 *
 * The orchestration intent lives here in the domain: each scene declares the
 * target climate, lighting, and blind state it represents. Applying a scene is
 * therefore a single, testable mapping rather than scattered UI logic.
 */
enum class EnergyScene(
    val targetCelsius: Int,
    val lightsOn: Boolean,
    val blinds: BlindPosition,
) {
    /** Wind down: cooler, lights off, blinds closed. */
    SLEEP(targetCelsius = 18, lightsOn = false, blinds = BlindPosition.CLOSED),

    /** Guest is out: energy-saving setpoint, everything off and closed. */
    AWAY(targetCelsius = 16, lightsOn = false, blinds = BlindPosition.CLOSED),

    /** Arrival: comfortable, lights on, blinds open. */
    WELCOME(targetCelsius = 22, lightsOn = true, blinds = BlindPosition.OPEN),
}
