package com.mews.guestroom.feature.controls.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.feature.controls.domain.FakeControlsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetTargetTemperatureUseCaseTest {

    private val repository = FakeControlsRepository()
    private val useCase = SetTargetTemperatureUseCase(repository)

    @Test
    fun invoke_belowMinimum_clampsToMinimum() = runTest {
        useCase(5)
        assertThat(repository.lastTargetCelsius).isEqualTo(SetTargetTemperatureUseCase.MIN_CELSIUS)
    }

    @Test
    fun invoke_aboveMaximum_clampsToMaximum() = runTest {
        useCase(40)
        assertThat(repository.lastTargetCelsius).isEqualTo(SetTargetTemperatureUseCase.MAX_CELSIUS)
    }

    @Test
    fun invoke_withinRange_passesValueUnchanged() = runTest {
        useCase(22)
        assertThat(repository.lastTargetCelsius).isEqualTo(22)
    }
}
