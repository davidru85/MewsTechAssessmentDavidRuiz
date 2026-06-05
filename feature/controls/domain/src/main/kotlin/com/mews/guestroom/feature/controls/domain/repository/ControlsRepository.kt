package com.mews.guestroom.feature.controls.domain.repository

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import kotlinx.coroutines.flow.Flow

/**
 * Feature-owned contract for observing and commanding the room.
 *
 * Implemented only by `:feature:controls:data`. The mock and live data sources
 * sit behind this same interface, so swapping them never touches domain or
 * presentation code.
 */
interface ControlsRepository {
    /** Continuous stream of the room state (state changes, drift, command results). */
    val controls: Flow<RoomControls>

    suspend fun setTargetTemperature(celsius: Int): DataResult<Unit>

    suspend fun toggleLight(id: String): DataResult<Unit>

    suspend fun setBlinds(position: BlindPosition): DataResult<Unit>

    suspend fun activateScene(scene: EnergyScene): DataResult<Unit>
}
