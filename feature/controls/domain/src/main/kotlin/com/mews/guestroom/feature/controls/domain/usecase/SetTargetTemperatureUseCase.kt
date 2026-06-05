package com.mews.guestroom.feature.controls.domain.usecase

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import javax.inject.Inject

/**
 * Sets the room's target temperature, clamping to a safe, sensible range so the
 * UI can never request an extreme setpoint.
 */
class SetTargetTemperatureUseCase @Inject constructor(
    private val repository: ControlsRepository,
) {
    suspend operator fun invoke(celsius: Int): DataResult<Unit> {
        val clamped = celsius.coerceIn(MIN_CELSIUS, MAX_CELSIUS)
        return repository.setTargetTemperature(clamped)
    }

    companion object {
        const val MIN_CELSIUS = 16
        const val MAX_CELSIUS = 28
    }
}
