package com.mews.guestroom.feature.controls.data.source

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MockControlsDataSourceTest {

    @Test
    fun controls_emitsInitialRoomStateAfterLoad() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            val initial = awaitItem()
            assertThat(initial.climate.targetCelsius).isEqualTo(21)
            assertThat(initial.lights).isNotEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleLight_flipsThatLightsState() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            val initial = awaitItem()
            assertThat(initial.lights.first { it.id == "bedside" }.isOn).isFalse()

            val result = dataSource.toggleLight("bedside")
            assertThat(result).isInstanceOf(DataResult.Success::class.java)

            val updated = awaitItem()
            assertThat(updated.lights.first { it.id == "bedside" }.isOn).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleLight_faultyDevice_returnsErrorAndDoesNotChangeState() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            awaitItem() // initial room state

            val result = dataSource.toggleLight("bathroom")

            assertThat(result).isInstanceOf(DataResult.Error::class.java)
            // A failed command must not mutate state: a StateFlow re-emits on any
            // change, so the absence of a new emission proves the state is unchanged.
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun activateScene_sleep_setsTargetTurnsLightsOffAndClosesBlinds() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            awaitItem() // initial

            dataSource.activateScene(EnergyScene.SLEEP)

            val updated = awaitItem()
            assertThat(updated.activeScene).isEqualTo(EnergyScene.SLEEP)
            assertThat(updated.climate.targetCelsius).isEqualTo(EnergyScene.SLEEP.targetCelsius)
            assertThat(updated.lights.none { it.isOn }).isTrue()
            assertThat(updated.blinds).isEqualTo(BlindPosition.CLOSED)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
