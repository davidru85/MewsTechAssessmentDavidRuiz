package com.mews.guestroom.feature.controls.data.source

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
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
    fun toggleLight_unknownId_returnsError() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        val result = dataSource.toggleLight("does-not-exist")

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
    }

    @Test
    fun setClimateMode_updatesModeAndClearsActiveScene() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            val initial = awaitItem()
            assertThat(initial.climate.mode).isEqualTo(ClimateMode.AUTO)

            // Enter a scene first, so we can prove that changing the mode clears it
            // (mode is a manual override, mutually exclusive with an active scene).
            dataSource.activateScene(EnergyScene.SLEEP)
            awaitItem()

            val result = dataSource.setClimateMode(ClimateMode.COOL)
            assertThat(result).isInstanceOf(DataResult.Success::class.java)

            val updated = awaitItem()
            assertThat(updated.climate.mode).isEqualTo(ClimateMode.COOL)
            assertThat(updated.activeScene).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun setTargetTemperature_preservesCurrentClimateMode() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            awaitItem() // initial, mode AUTO

            dataSource.setClimateMode(ClimateMode.COOL)
            awaitItem()

            // Adjusting the target must not silently reset the user's chosen mode.
            dataSource.setTargetTemperature(22)
            val updated = awaitItem()
            assertThat(updated.climate.mode).isEqualTo(ClimateMode.COOL)
            assertThat(updated.climate.targetCelsius).isEqualTo(22)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun temperatureDrift_inCoolMode_doesNotHeatTowardHigherTarget() = runTest {
        val dataSource = MockControlsDataSource(backgroundScope)

        dataSource.controls.test {
            val initial = awaitItem() // current 24.0
            val startTemp = initial.climate.currentCelsius

            dataSource.setClimateMode(ClimateMode.COOL)
            awaitItem()
            // Target above the current room temp: cooling must never push it warmer.
            dataSource.setTargetTemperature(26)
            assertThat(awaitItem().climate.currentCelsius).isEqualTo(startTemp)

            // Let several drift ticks pass; in COOL mode the room cannot heat up,
            // so the state must not change (a StateFlow re-emits only on change).
            advanceTimeBy(10_000)
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
