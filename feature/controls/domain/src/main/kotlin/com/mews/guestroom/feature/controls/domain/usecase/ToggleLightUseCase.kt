package com.mews.guestroom.feature.controls.domain.usecase

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import javax.inject.Inject

/** Toggles a single light on/off. */
class ToggleLightUseCase @Inject constructor(
    private val repository: ControlsRepository,
) {
    suspend operator fun invoke(id: String): DataResult<Unit> = repository.toggleLight(id)
}
