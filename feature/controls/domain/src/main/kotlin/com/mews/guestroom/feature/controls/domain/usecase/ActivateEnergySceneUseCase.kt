package com.mews.guestroom.feature.controls.domain.usecase

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import javax.inject.Inject

/**
 * Applies a one-tap energy scene, orchestrating climate, lights, and blinds in
 * a single command (see [EnergyScene] for what each scene represents).
 */
class ActivateEnergySceneUseCase @Inject constructor(
    private val repository: ControlsRepository,
) {
    suspend operator fun invoke(scene: EnergyScene): DataResult<Unit> =
        repository.activateScene(scene)
}
