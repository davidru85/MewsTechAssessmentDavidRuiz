package com.mews.guestroom.feature.controls.domain.usecase

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import javax.inject.Inject

/**
 * Sets the room's climate mode (e.g. Auto, Cooling). The mode is a constrained
 * enum, so there is nothing to validate — this is a thin, intention-revealing
 * pass-through to the repository, kept as a use case for a consistent UI seam.
 */
class SetClimateModeUseCase @Inject constructor(
    private val repository: ControlsRepository,
) {
    suspend operator fun invoke(mode: ClimateMode): DataResult<Unit> =
        repository.setClimateMode(mode)
}
