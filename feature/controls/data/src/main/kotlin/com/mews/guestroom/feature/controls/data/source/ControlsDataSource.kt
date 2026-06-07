package com.mews.guestroom.feature.controls.data.source

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import kotlinx.coroutines.flow.Flow

/**
 * Data-layer seam for the room state. The mock implementation simulates a room
 * hub in memory; a future live implementation would talk to MQTT/BLE behind the
 * exact same interface, with no change to the repository, domain, or UI.
 */
interface ControlsDataSource {
    val controls: Flow<RoomControls>

    suspend fun setTargetTemperature(celsius: Int): DataResult<Unit>

    suspend fun setClimateMode(mode: ClimateMode): DataResult<Unit>

    suspend fun toggleLight(id: String): DataResult<Unit>

    suspend fun setBlinds(position: BlindPosition): DataResult<Unit>

    suspend fun activateScene(scene: EnergyScene): DataResult<Unit>
}
