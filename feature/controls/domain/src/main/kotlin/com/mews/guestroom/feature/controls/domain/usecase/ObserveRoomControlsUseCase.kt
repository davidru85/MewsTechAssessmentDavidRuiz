package com.mews.guestroom.feature.controls.domain.usecase

import com.mews.guestroom.feature.controls.domain.model.RoomControls
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Streams the current room state for the dashboard to render. */
class ObserveRoomControlsUseCase @Inject constructor(
    private val repository: ControlsRepository,
) {
    operator fun invoke(): Flow<RoomControls> = repository.controls
}
