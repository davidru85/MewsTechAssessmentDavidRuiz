package com.mews.guestroom.feature.controls.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mews.guestroom.core.ui.theme.GuestRoomTheme
import com.mews.guestroom.feature.controls.domain.model.BlindPosition
import com.mews.guestroom.feature.controls.domain.model.Climate
import com.mews.guestroom.feature.controls.domain.model.ClimateMode
import com.mews.guestroom.feature.controls.domain.model.EnergyScene
import com.mews.guestroom.feature.controls.domain.model.LightControl
import com.mews.guestroom.feature.controls.domain.model.RoomControls
import kotlin.math.roundToInt

/** User actions the dashboard can trigger, grouped to keep composable signatures small. */
data class DashboardActions(
    val onTargetTemperatureChange: (Int) -> Unit,
    val onToggleLight: (String) -> Unit,
    val onSetBlinds: (BlindPosition) -> Unit,
    val onActivateScene: (EnergyScene) -> Unit,
)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    DashboardScaffold(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        actions = DashboardActions(
            onTargetTemperatureChange = viewModel::onTargetTemperatureChange,
            onToggleLight = viewModel::onToggleLight,
            onSetBlinds = viewModel::onSetBlinds,
            onActivateScene = viewModel::onActivateScene,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScaffold(
    uiState: DashboardUiState,
    snackbarHostState: SnackbarHostState,
    actions: DashboardActions,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Smart Guest Room") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (uiState) {
                DashboardUiState.Loading -> CenteredMessage { CircularProgressIndicator() }
                is DashboardUiState.Content -> DashboardContent(state = uiState, actions = actions)
            }
        }
    }
}

@Composable
private fun DashboardContent(state: DashboardUiState.Content, actions: DashboardActions) {
    if (state.isActionInProgress) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ClimateCard(state.controls.climate, actions.onTargetTemperatureChange)
        ScenesCard(state.controls.activeScene, actions.onActivateScene)
        LightsCard(state.controls.lights, actions.onToggleLight)
        BlindsCard(state.controls.blinds, actions.onSetBlinds)
    }
}

@Composable
private fun ClimateCard(climate: Climate, onTargetTemperatureChange: (Int) -> Unit) {
    SectionCard(title = "Climate") {
        // Track the slider locally during a drag so the thumb moves smoothly and we
        // dispatch a single command on release — not one per frame. Re-keyed on the
        // confirmed target so backend updates (and scene activations) re-sync the thumb.
        var sliderValue by remember(climate.targetCelsius) {
            mutableFloatStateOf(climate.targetCelsius.toFloat())
        }
        Text(
            text = "Now ${climate.currentCelsius}°C  ·  Target ${sliderValue.roundToInt()}°C",
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onTargetTemperatureChange(sliderValue.roundToInt()) },
            valueRange = MIN_TEMP..MAX_TEMP,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScenesCard(activeScene: EnergyScene?, onActivateScene: (EnergyScene) -> Unit) {
    SectionCard(title = "Energy scenes") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EnergyScene.entries.forEach { scene ->
                FilterChip(
                    selected = activeScene == scene,
                    onClick = { onActivateScene(scene) },
                    label = { Text(scene.label()) },
                )
            }
        }
    }
}

@Composable
private fun LightsCard(lights: List<LightControl>, onToggleLight: (String) -> Unit) {
    SectionCard(title = "Lights") {
        lights.forEach { light ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(light.name, style = MaterialTheme.typography.bodyLarge)
                Switch(checked = light.isOn, onCheckedChange = { onToggleLight(light.id) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlindsCard(blinds: BlindPosition, onSetBlinds: (BlindPosition) -> Unit) {
    SectionCard(title = "Blinds") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlindPosition.entries.forEach { position ->
                FilterChip(
                    selected = blinds == position,
                    onClick = { onSetBlinds(position) },
                    label = { Text(position.name.label()) },
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun CenteredMessage(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

private fun EnergyScene.label(): String = name.label()

private fun String.label(): String = lowercase().replaceFirstChar { it.uppercase() }

private const val MIN_TEMP = 16f
private const val MAX_TEMP = 28f

@Preview
@Composable
private fun DashboardContentPreview() {
    GuestRoomTheme {
        DashboardContent(
            state = DashboardUiState.Content(
                controls = RoomControls(
                    climate = Climate(currentCelsius = 23.4, targetCelsius = 21, mode = ClimateMode.AUTO),
                    lights = listOf(
                        LightControl(id = "ceiling", name = "Ceiling", isOn = true),
                        LightControl(id = "bedside", name = "Bedside", isOn = false),
                    ),
                    blinds = BlindPosition.OPEN,
                    activeScene = null,
                ),
                isActionInProgress = false,
            ),
            actions = DashboardActions({}, {}, {}, {}),
        )
    }
}
