package com.mews.guestroom.feature.controls.domain

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.Climate
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.LightControl
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Shared in-memory fake repository for domain and presentation tests.
 *
 * Lives in this module's test fixtures so both `:domain` and `:presentation`
 * tests consume a single, contract-accurate double instead of duplicating it.
 */
class FakeControlsRepository(
    initial: RoomControls = sampleRoomControls(),
) : ControlsRepository {

    val state = MutableStateFlow(initial)

    var lastTargetCelsius: Int? = null
    var lastToggledLightId: String? = null
    var lastBlinds: BlindPosition? = null
    var lastScene: EnergyScene? = null

    var nextResult: DataResult<Unit> = DataResult.Success(Unit)

    /** When true, each command suspends on a per-call gate until [completeCommand] releases it. */
    var gateCommands: Boolean = false

    /** When true, the next command throws instead of returning a result. */
    var throwOnCommand: Boolean = false

    private val gates = mutableListOf<CompletableDeferred<Unit>>()

    override val controls: Flow<RoomControls> = state

    override suspend fun setTargetTemperature(celsius: Int): DataResult<Unit> {
        lastTargetCelsius = celsius
        return result()
    }

    override suspend fun toggleLight(id: String): DataResult<Unit> {
        lastToggledLightId = id
        return result()
    }

    override suspend fun setBlinds(position: BlindPosition): DataResult<Unit> {
        lastBlinds = position
        return result()
    }

    override suspend fun activateScene(scene: EnergyScene): DataResult<Unit> {
        lastScene = scene
        return result()
    }

    /** Releases the n-th in-flight gated command. */
    fun completeCommand(index: Int) {
        gates[index].complete(Unit)
    }

    private suspend fun result(): DataResult<Unit> {
        if (throwOnCommand) error("Simulated command failure")
        if (gateCommands) {
            val gate = CompletableDeferred<Unit>()
            gates += gate
            gate.await()
        }
        return nextResult
    }
}

fun sampleRoomControls(): RoomControls = RoomControls(
    climate = Climate(currentCelsius = 23.0, targetCelsius = 21, mode = ClimateMode.AUTO),
    lights = listOf(
        LightControl(id = "ceiling", name = "Ceiling", isOn = true),
        LightControl(id = "bedside", name = "Bedside", isOn = false),
    ),
    blinds = BlindPosition.OPEN,
    activeScene = null,
)
