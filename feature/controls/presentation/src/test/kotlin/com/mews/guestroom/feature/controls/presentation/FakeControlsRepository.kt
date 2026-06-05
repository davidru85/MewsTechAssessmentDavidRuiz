package com.mews.guestroom.feature.controls.presentation

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.Climate
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.LightControl
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory fake repository for ViewModel tests. */
class FakeControlsRepository(
    initial: RoomControls = sampleRoomControls(),
) : ControlsRepository {

    val state = MutableStateFlow(initial)

    var lastTargetCelsius: Int? = null
    var lastToggledLightId: String? = null
    var lastBlinds: BlindPosition? = null
    var lastScene: EnergyScene? = null

    var nextResult: DataResult<Unit> = DataResult.Success(Unit)

    override val controls: Flow<RoomControls> = state

    override suspend fun setTargetTemperature(celsius: Int): DataResult<Unit> {
        lastTargetCelsius = celsius
        return nextResult
    }

    override suspend fun toggleLight(id: String): DataResult<Unit> {
        lastToggledLightId = id
        return nextResult
    }

    override suspend fun setBlinds(position: BlindPosition): DataResult<Unit> {
        lastBlinds = position
        return nextResult
    }

    override suspend fun activateScene(scene: EnergyScene): DataResult<Unit> {
        lastScene = scene
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
