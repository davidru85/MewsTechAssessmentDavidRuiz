package com.mews.guestroom.feature.controls.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EnergySceneTest {

    @Test
    fun sleep_windsDownTheRoom() {
        assertThat(EnergyScene.SLEEP.targetCelsius).isEqualTo(18)
        assertThat(EnergyScene.SLEEP.lightsOn).isFalse()
        assertThat(EnergyScene.SLEEP.blinds).isEqualTo(BlindPosition.CLOSED)
    }

    @Test
    fun welcome_makesTheRoomInviting() {
        assertThat(EnergyScene.WELCOME.lightsOn).isTrue()
        assertThat(EnergyScene.WELCOME.blinds).isEqualTo(BlindPosition.OPEN)
    }
}
