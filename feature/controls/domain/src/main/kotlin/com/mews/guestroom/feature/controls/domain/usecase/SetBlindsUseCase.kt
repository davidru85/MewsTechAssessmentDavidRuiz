package com.mews.guestroom.feature.controls.domain.usecase

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import javax.inject.Inject

/** Moves the blinds to an explicit position. */
class SetBlindsUseCase @Inject constructor(
    private val repository: ControlsRepository,
) {
    suspend operator fun invoke(position: BlindPosition): DataResult<Unit> =
        repository.setBlinds(position)
}
