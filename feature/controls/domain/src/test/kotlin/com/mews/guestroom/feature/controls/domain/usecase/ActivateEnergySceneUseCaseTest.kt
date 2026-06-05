package com.mews.guestroom.feature.controls.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.FakeControlsRepository
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ActivateEnergySceneUseCaseTest {

    private val repository = FakeControlsRepository()
    private val useCase = ActivateEnergySceneUseCase(repository)

    @Test
    fun invoke_forwardsSceneToRepository() = runTest {
        useCase(EnergyScene.SLEEP)
        assertThat(repository.lastScene).isEqualTo(EnergyScene.SLEEP)
    }

    @Test
    fun invoke_propagatesError() = runTest {
        repository.nextResult = DataResult.Error("unreachable")
        val result = useCase(EnergyScene.AWAY)
        assertThat(result).isInstanceOf(DataResult.Error::class.java)
    }
}
