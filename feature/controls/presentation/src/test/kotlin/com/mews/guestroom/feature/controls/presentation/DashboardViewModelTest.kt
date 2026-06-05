package com.mews.guestroom.feature.controls.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.FakeControlsRepository
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.sampleRoomControls
import com.mews.guestroom.feature.controls.domain.usecase.ActivateEnergySceneUseCase
import com.mews.guestroom.feature.controls.domain.usecase.ObserveRoomControlsUseCase
import com.mews.guestroom.feature.controls.domain.usecase.SetBlindsUseCase
import com.mews.guestroom.feature.controls.domain.usecase.SetTargetTemperatureUseCase
import com.mews.guestroom.feature.controls.domain.usecase.ToggleLightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(repository: FakeControlsRepository) = DashboardViewModel(
        observeRoomControls = ObserveRoomControlsUseCase(repository),
        setTargetTemperature = SetTargetTemperatureUseCase(repository),
        toggleLight = ToggleLightUseCase(repository),
        setBlinds = SetBlindsUseCase(repository),
        activateScene = ActivateEnergySceneUseCase(repository),
    )

    @Test
    fun uiState_startsLoadingThenShowsContent() = runTest(dispatcher) {
        val viewModel = createViewModel(FakeControlsRepository())

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(DashboardUiState.Loading)
            assertThat(awaitItem()).isInstanceOf(DashboardUiState.Content::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_roomWithoutLights_rendersContentNotEmpty() = runTest(dispatcher) {
        val noLightsRoom = sampleRoomControls().copy(
            lights = emptyList(),
            climate = sampleRoomControls().climate.copy(mode = ClimateMode.OFF),
        )
        val viewModel = createViewModel(FakeControlsRepository(noLightsRoom))

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(DashboardUiState.Loading)
            assertThat(awaitItem()).isInstanceOf(DashboardUiState.Content::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onToggleLight_whenRepositoryFails_emitsErrorMessage() = runTest(dispatcher) {
        val repository = FakeControlsRepository().apply {
            nextResult = DataResult.Error("Bathroom light is unreachable.")
        }
        val viewModel = createViewModel(repository)

        viewModel.events.test {
            viewModel.onToggleLight("bathroom")

            val event = awaitItem()
            assertThat(event).isInstanceOf(DashboardEvent.ShowMessage::class.java)
            assertThat((event as DashboardEvent.ShowMessage).message).contains("unreachable")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onTargetTemperatureChange_forwardsValueToRepository() = runTest(dispatcher) {
        val repository = FakeControlsRepository()
        val viewModel = createViewModel(repository)

        viewModel.onTargetTemperatureChange(22)
        advanceUntilIdle()

        assertThat(repository.lastTargetCelsius).isEqualTo(22)
    }

    @Test
    fun runCommand_concurrentCommands_staysBusyUntilAllComplete() = runTest(dispatcher) {
        val repository = FakeControlsRepository().apply { gateCommands = true }
        val viewModel = createViewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onToggleLight("ceiling")
        viewModel.onSetBlinds(BlindPosition.OPEN)
        advanceUntilIdle()
        assertThat(contentOf(viewModel).isActionInProgress).isTrue()

        repository.completeCommand(0)
        advanceUntilIdle()
        // The second command is still running, so the dashboard must stay busy.
        assertThat(contentOf(viewModel).isActionInProgress).isTrue()

        repository.completeCommand(1)
        advanceUntilIdle()
        assertThat(contentOf(viewModel).isActionInProgress).isFalse()
    }

    @Test
    fun runCommand_whenCommandThrows_clearsBusyAndEmitsError() = runTest(dispatcher) {
        val repository = FakeControlsRepository().apply { throwOnCommand = true }
        val viewModel = createViewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onToggleLight("ceiling")
            advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(DashboardEvent.ShowMessage::class.java)
            cancelAndIgnoreRemainingEvents()
        }
        // The busy state must be released even though the command threw.
        assertThat(contentOf(viewModel).isActionInProgress).isFalse()
    }

    private fun contentOf(viewModel: DashboardViewModel): DashboardUiState.Content =
        viewModel.uiState.value as DashboardUiState.Content
}
