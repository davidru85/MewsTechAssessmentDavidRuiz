package com.mews.guestroom.feature.controls.data.source

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.data.di.ControlsScope
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.Climate
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.LightControl
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * In-memory room simulation used by the prototype.
 *
 * Makes the demo feel alive without any backend or hardware:
 *  - a short initial load delay so the dashboard shows its loading state;
 *  - per-command latency so toggles show in-flight feedback;
 *  - a background drift loop nudging the current temperature toward the target;
 *  - one intentionally "faulty" device so the error path is demonstrable.
 */
@Singleton
class MockControlsDataSource @Inject constructor(
    @ControlsScope private val scope: CoroutineScope,
) : ControlsDataSource {

    private val state = MutableStateFlow(initialState())

    override val controls: Flow<RoomControls> = flow {
        delay(INITIAL_LOAD_DELAY_MS)
        emitAll(state)
    }

    init {
        startTemperatureDrift()
    }

    override suspend fun setTargetTemperature(celsius: Int): DataResult<Unit> {
        delay(COMMAND_LATENCY_MS)
        // Preserve the user's chosen mode — only the target changes here.
        manualUpdate { it.copy(climate = it.climate.copy(targetCelsius = celsius)) }
        return DataResult.Success(Unit)
    }

    override suspend fun setClimateMode(mode: ClimateMode): DataResult<Unit> {
        delay(COMMAND_LATENCY_MS)
        manualUpdate { it.copy(climate = it.climate.copy(mode = mode)) }
        return DataResult.Success(Unit)
    }

    override suspend fun toggleLight(id: String): DataResult<Unit> {
        delay(COMMAND_LATENCY_MS)
        if (id in FAULTY_LIGHT_IDS) {
            return DataResult.Error("Bathroom light is unreachable. Please try again.")
        }
        if (state.value.lights.none { it.id == id }) {
            return DataResult.Error("Unknown light: $id")
        }
        manualUpdate { current ->
            current.copy(
                lights = current.lights.map { light ->
                    if (light.id == id) light.copy(isOn = !light.isOn) else light
                },
            )
        }
        return DataResult.Success(Unit)
    }

    override suspend fun setBlinds(position: BlindPosition): DataResult<Unit> {
        delay(COMMAND_LATENCY_MS)
        manualUpdate { it.copy(blinds = position) }
        return DataResult.Success(Unit)
    }

    /**
     * Applies a manual control change. Any manual command implicitly cancels an
     * active scene, so that invariant lives here once — a new command physically
     * can't forget it.
     */
    private fun manualUpdate(transform: (RoomControls) -> RoomControls) {
        state.update { current -> transform(current).copy(activeScene = null) }
    }

    override suspend fun activateScene(scene: EnergyScene): DataResult<Unit> {
        delay(COMMAND_LATENCY_MS)
        state.update { current ->
            current.copy(
                climate = current.climate.copy(targetCelsius = scene.targetCelsius, mode = ClimateMode.AUTO),
                lights = current.lights.map { it.copy(isOn = scene.lightsOn) },
                blinds = scene.blinds,
                activeScene = scene,
            )
        }
        return DataResult.Success(Unit)
    }

    private fun startTemperatureDrift() {
        scope.launch {
            while (isActive) {
                delay(DRIFT_INTERVAL_MS)
                state.update { current ->
                    val climate = current.climate
                    val diff = climate.targetCelsius - climate.currentCelsius
                    // In COOL mode the AC can only cool, so it never drifts the room
                    // warmer even if the target is set above the current temperature.
                    val coolingCannotHeat = climate.mode == ClimateMode.COOL && diff > 0
                    if (climate.mode == ClimateMode.OFF || coolingCannotHeat || abs(diff) < DRIFT_STEP) {
                        current
                    } else {
                        val next = climate.currentCelsius + DRIFT_STEP * sign(diff)
                        current.copy(climate = climate.copy(currentCelsius = next.roundTo1Decimal()))
                    }
                }
            }
        }
    }

    private fun Double.roundTo1Decimal(): Double = (this * TENTHS).roundToInt() / TENTHS

    private companion object {
        const val INITIAL_LOAD_DELAY_MS = 600L
        const val COMMAND_LATENCY_MS = 350L
        const val DRIFT_INTERVAL_MS = 1_500L
        const val DRIFT_STEP = 0.3
        const val TENTHS = 10.0
        val FAULTY_LIGHT_IDS = setOf("bathroom")

        fun initialState() = RoomControls(
            climate = Climate(currentCelsius = 24.0, targetCelsius = 21, mode = ClimateMode.AUTO),
            lights = listOf(
                LightControl(id = "ceiling", name = "Ceiling", isOn = true),
                LightControl(id = "bedside", name = "Bedside", isOn = false),
                LightControl(id = "bathroom", name = "Bathroom", isOn = false),
            ),
            blinds = BlindPosition.OPEN,
            activeScene = null,
        )
    }
}
