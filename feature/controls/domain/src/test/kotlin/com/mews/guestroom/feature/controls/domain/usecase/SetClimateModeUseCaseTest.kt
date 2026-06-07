package com.mews.guestroom.feature.controls.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.FakeControlsRepository
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetClimateModeUseCaseTest {

    private val repository = FakeControlsRepository()
    private val useCase = SetClimateModeUseCase(repository)

    @Test
    fun invoke_forwardsModeToRepository() = runTest {
        useCase(ClimateMode.COOL)
        assertThat(repository.lastClimateMode).isEqualTo(ClimateMode.COOL)
    }

    @Test
    fun invoke_propagatesRepositoryError() = runTest {
        repository.nextResult = DataResult.Error("boom")
        val result = useCase(ClimateMode.AUTO)
        assertThat(result).isInstanceOf(DataResult.Error::class.java)
    }
}
