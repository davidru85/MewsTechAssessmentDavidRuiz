package com.mews.guestroom.feature.controls.data.source

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mews.guestroom.core.common.result.DataResult
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * The live source is an honest stub in this build: no room hub is wired, so the
 * state stream is empty (the dashboard stays in Loading) and every command reports
 * a clear "not connected" error rather than silently succeeding.
 */
class LiveControlsDataSourceTest {

    private val notConnected = "Live room hub is not connected in this build."

    @Test
    fun controls_emitsNothingAndCompletes() = runTest {
        LiveControlsDataSource().controls.test {
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
