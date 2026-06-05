package com.mews.guestroom.feature.controls.data.source

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * The live source is an honest stub in this build: no room hub is wired, so it
 * emits a single neutral "disconnected" snapshot (climate OFF) so the dashboard
 * renders rather than hanging in Loading, and every command reports a clear "not
 * connected" error — making the unwired state visible and the error path reachable.
 */
class LiveControlsDataSourceTest {

    private val notConnected = LiveControlsDataSource.NOT_CONNECTED

    @Test
    fun controls_emitsSingleDisconnectedSnapshotThenCompletes() = runTest {
        LiveControlsDataSource().controls.test {
            val snapshot = awaitItem()
            assertThat(snapshot.climate.mode).isEqualTo(ClimateMode.OFF)
            assertThat(snapshot.lights).isNotEmpty()
            assertThat(snapshot.activeScene).isNull()
            awaitComplete()
        }
    }

    @Test
    fun setTargetTemperature_returnsNotConnectedError() = runTest {
        val result = LiveControlsDataSource().setTargetTemperature(21)

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
        assertThat((result as DataResult.Error).message).isEqualTo(notConnected)
    }

    @Test
    fun toggleLight_returnsNotConnectedError() = runTest {
        val result = LiveControlsDataSource().toggleLight("ceiling")

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
        assertThat((result as DataResult.Error).message).isEqualTo(notConnected)
    }

    @Test
    fun setBlinds_returnsNotConnectedError() = runTest {
        val result = LiveControlsDataSource().setBlinds(BlindPosition.CLOSED)

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
        assertThat((result as DataResult.Error).message).isEqualTo(notConnected)
    }

    @Test
    fun activateScene_returnsNotConnectedError() = runTest {
        val result = LiveControlsDataSource().activateScene(EnergyScene.SLEEP)

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
        assertThat((result as DataResult.Error).message).isEqualTo(notConnected)
    }
}
