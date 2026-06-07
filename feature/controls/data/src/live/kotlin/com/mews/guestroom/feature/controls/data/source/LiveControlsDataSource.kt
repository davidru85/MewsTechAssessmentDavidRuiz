package com.mews.guestroom.feature.controls.data.source

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.Climate
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.LightControl
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Live room-hub data source for the `live` flavor.
 *
 * The real MQTT/BLE/PMS integration is Phase 2 work; in this build there is no hub,
 * so the source is an *honest* stub rather than a silent success. It emits a single
 * neutral "disconnected" snapshot (climate [ClimateMode.OFF]) so the dashboard renders
 * the room rather than hanging in Loading, and every command reports a clear "not
 * connected" error. This proves the production seam — selecting the `live` flavor
 * swaps the bound [ControlsDataSource] with zero changes to domain, presentation, or
 * `:app` — while keeping the unfinished, unwired state unmistakable to the user.
 */
class LiveControlsDataSource @Inject constructor() : ControlsDataSource {

    override val controls: Flow<RoomControls> = flowOf(disconnectedSnapshot())

    override suspend fun setTargetTemperature(celsius: Int): DataResult<Unit> = notConnected()

    override suspend fun setClimateMode(mode: ClimateMode): DataResult<Unit> = notConnected()

    override suspend fun toggleLight(id: String): DataResult<Unit> = notConnected()

    override suspend fun setBlinds(position: BlindPosition): DataResult<Unit> = notConnected()

    override suspend fun activateScene(scene: EnergyScene): DataResult<Unit> = notConnected()

    private fun notConnected(): DataResult<Unit> = DataResult.Error(NOT_CONNECTED)

    companion object {
        /** Single source of truth for the stub's error text (asserted by tests too). */
        const val NOT_CONNECTED = "Live room hub is not connected in this build."

        /**
         * A placeholder room snapshot for the unwired live build: climate OFF and
         * lights off, so the UI renders and any command immediately surfaces
         * [NOT_CONNECTED] rather than silently doing nothing.
         */
        private fun disconnectedSnapshot() = RoomControls(
            climate = Climate(currentCelsius = 21.0, targetCelsius = 21, mode = ClimateMode.OFF),
            lights = listOf(
                LightControl(id = "ceiling", name = "Ceiling", isOn = false),
                LightControl(id = "bedside", name = "Bedside", isOn = false),
                LightControl(id = "bathroom", name = "Bathroom", isOn = false),
            ),
            blinds = BlindPosition.OPEN,
            activeScene = null,
        )
    }
}
