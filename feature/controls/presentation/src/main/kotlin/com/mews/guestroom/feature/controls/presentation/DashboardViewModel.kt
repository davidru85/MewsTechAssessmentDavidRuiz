package com.mews.guestroom.feature.controls.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.usecase.ActivateEnergySceneUseCase
import com.mews.guestroom.feature.controls.domain.usecase.ObserveRoomControlsUseCase
import com.mews.guestroom.feature.controls.domain.usecase.SetBlindsUseCase
import com.mews.guestroom.feature.controls.domain.usecase.SetTargetTemperatureUseCase
import com.mews.guestroom.feature.controls.domain.usecase.ToggleLightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Room Dashboard. Exposes a single [uiState] stream for rendering and
 * a one-shot [events] stream for transient messages (e.g. a failed command).
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeRoomControls: ObserveRoomControlsUseCase,
    private val setTargetTemperature: SetTargetTemperatureUseCase,
    private val toggleLight: ToggleLightUseCase,
    private val setBlinds: SetBlindsUseCase,
    private val activateScene: ActivateEnergySceneUseCase,
) : ViewModel() {

    // Count of in-flight commands, so overlapping commands keep the dashboard
    // busy until the last one finishes (a plain boolean would clear early).
    private val activeCommands = MutableStateFlow(0)

    val uiState: StateFlow<DashboardUiState> =
        combine(observeRoomControls(), activeCommands) { controls, active ->
            DashboardUiState.Content(controls = controls, isActionInProgress = active > 0)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = DashboardUiState.Loading,
        )

    private val _events = MutableSharedFlow<DashboardEvent>()
    val events: SharedFlow<DashboardEvent> = _events.asSharedFlow()

    fun onTargetTemperatureChange(celsius: Int) = runCommand { setTargetTemperature(celsius) }

    fun onToggleLight(id: String) = runCommand { toggleLight(id) }

    fun onSetBlinds(position: BlindPosition) = runCommand { setBlinds(position) }

    fun onActivateScene(scene: EnergyScene) = runCommand { activateScene(scene) }

    @Suppress("TooGenericExceptionCaught")
    private fun runCommand(block: suspend () -> DataResult<Unit>) {
        viewModelScope.launch {
            activeCommands.update { it + 1 }
            try {
                when (val result = block()) {
                    is DataResult.Error -> _events.emit(DashboardEvent.ShowMessage(result.message))
                    is DataResult.Success -> Unit
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _events.emit(DashboardEvent.ShowMessage(error.message ?: GENERIC_ERROR))
            } finally {
                // Always release the busy state, even if the command failed or threw.
                activeCommands.update { it - 1 }
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        const val GENERIC_ERROR = "Something went wrong. Please try again."
    }
}
