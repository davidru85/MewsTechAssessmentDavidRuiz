package com.mews.guestroom.feature.controls.data.repository

import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.data.source.ControlsDataSource
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Thin repository that delegates to whichever [ControlsDataSource] is bound
 * (mock today, live in production). The delegation point is exactly where the
 * prototype-to-production seam lives.
 */
class ControlsRepositoryImpl @Inject constructor(
    private val dataSource: ControlsDataSource,
) : ControlsRepository {

    override val controls: Flow<RoomControls> = dataSource.controls

    override suspend fun setTargetTemperature(celsius: Int): DataResult<Unit> =
        dataSource.setTargetTemperature(celsius)

    override suspend fun toggleLight(id: String): DataResult<Unit> =
        dataSource.toggleLight(id)

    override suspend fun setBlinds(position: BlindPosition): DataResult<Unit> =
        dataSource.setBlinds(position)

    override suspend fun activateScene(scene: EnergyScene): DataResult<Unit> =
        dataSource.activateScene(scene)
}
