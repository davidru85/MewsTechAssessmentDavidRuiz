package com.mews.guestroom.feature.controls.data.source

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Live room-hub data source — placeholder until the Phase 2 integration lands.
 *
 * RED: contract only; behaviour is implemented in the GREEN phase.
 */
class LiveControlsDataSource @Inject constructor() : ControlsDataSource {

    override val controls: Flow<RoomControls>
        get() = TODO("Phase 2: live room-hub stream")

    override suspend fun setTargetTemperature(celsius: Int): DataResult<Unit> =
        TODO("Phase 2: live room-hub command")

    override suspend fun toggleLight(id: String): DataResult<Unit> =
        TODO("Phase 2: live room-hub command")

    override suspend fun setBlinds(position: BlindPosition): DataResult<Unit> =
        TODO("Phase 2: live room-hub command")

    override suspend fun activateScene(scene: EnergyScene): DataResult<Unit> =
        TODO("Phase 2: live room-hub command")
}
