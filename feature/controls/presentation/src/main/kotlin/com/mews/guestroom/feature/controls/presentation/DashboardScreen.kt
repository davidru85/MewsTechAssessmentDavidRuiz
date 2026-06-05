package com.mews.guestroom.feature.controls.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RoomService
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
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

    LaunchedSnackbar(viewModel, snackbarHostState)

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

@Composable
private fun LaunchedSnackbar(viewModel: DashboardViewModel, snackbarHostState: SnackbarHostState) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScaffold(
    uiState: DashboardUiState,
    snackbarHostState: SnackbarHostState,
    actions: DashboardActions,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { RoomTopBar() },
        bottomBar = { RoomBottomNav() },
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
            .padding(horizontal = OUTER_MARGIN, vertical = CARD_GAP),
        verticalArrangement = Arrangement.spacedBy(CARD_GAP),
    ) {
        ClimateSection(state.controls.climate, actions.onTargetTemperatureChange)
        AtmosphereSection(state.controls.activeScene, actions.onActivateScene)
        LightingSection(state.controls.lights, actions.onToggleLight)
        PrivacyAndViewSection(state.controls.blinds, actions.onSetBlinds)
    }
}

// --- 1. Top App Bar (DESIGN.md §1) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(10.dp))
                Text("Suite 402", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(12.dp))
                InSyncPill()
            }
        },
        actions = {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp),
            )
            Icon(
                imageVector = Icons.Outlined.Wifi,
                contentDescription = "Connection strength",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp),
            )
        },
    )
}

/** "IN SYNC" pill with a gently pulsing green indicator (DESIGN.md §1, real-time sync status). */
@Composable
private fun InSyncPill() {
    val transition = rememberInfiniteTransition(label = "sync-pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sync-pulse-alpha",
    )
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(pulse)
                    .background(SyncGreen, CircleShape),
            )
            Text(
                text = "IN SYNC",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// --- 2. Climate Control (DESIGN.md §2) ---

@Composable
private fun ClimateSection(climate: Climate, onTargetTemperatureChange: (Int) -> Unit) {
    SectionCard(title = "Climate Control") {
        // Local thumb position during a drag so it moves smoothly; we dispatch a single command on
        // release. Re-keyed on the confirmed target so backend/scene updates re-sync the thumb.
        var sliderValue by remember(climate.targetCelsius) {
            mutableFloatStateOf(climate.targetCelsius.toFloat())
        }

        // Primary display: large central temperature + label.
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${climate.currentCelsius.roundToInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Inside Temp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        TargetSlider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onTargetTemperatureChange(sliderValue.roundToInt()) },
        )

        ClimateModeButtons(climate.mode)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val fraction = ((value - MIN_TEMP) / (MAX_TEMP - MIN_TEMP)).coerceIn(0f, 1f)
    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier.fillMaxWidth()) {
        // Floating "Target XX°C" pill that tracks the thumb horizontally.
        Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(fraction)
                    .wrapContentSize(Alignment.CenterEnd),
            ) {
                Text(
                    text = "Target ${value.roundToInt()}°C",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = MIN_TEMP..MAX_TEMP,
            interactionSource = interactionSource,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    thumbSize = DpSize(28.dp, 28.dp),
                )
            },
        )
    }
}

@Composable
private fun ClimateModeButtons(mode: ClimateMode) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        // NOTE: climate mode is display-only for now — the active state reflects the live mode, but
        // dispatching a mode change needs a SetClimateMode use case (domain/data + tests), tracked
        // as the next slice. Kept here so the layout matches DESIGN.md §2.
        ModeButton(
            label = "Auto Fan",
            icon = Icons.Filled.Air,
            active = mode == ClimateMode.AUTO,
            modifier = Modifier.weight(1f),
        )
        ModeButton(
            label = "Cooling",
            icon = Icons.Filled.AcUnit,
            active = mode == ClimateMode.COOL,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ModeButton(label: String, icon: ImageVector, active: Boolean, modifier: Modifier = Modifier) {
    val content = @Composable {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
    if (active) {
        Button(
            onClick = {},
            modifier = modifier.heightIn(min = 52.dp),
            shape = MaterialTheme.shapes.medium,
        ) { content() }
    } else {
        OutlinedButton(
            onClick = {},
            modifier = modifier.heightIn(min = 52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) { content() }
    }
}

// --- 3. Atmosphere / Moods (DESIGN.md §3) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AtmosphereSection(activeScene: EnergyScene?, onActivateScene: (EnergyScene) -> Unit) {
    SectionCard(title = "Atmosphere") {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val scenes = EnergyScene.entries
            scenes.forEachIndexed { index, scene ->
                SegmentedButton(
                    selected = activeScene == scene,
                    onClick = { onActivateScene(scene) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = scenes.size),
                    icon = {
                        Icon(
                            imageVector = scene.icon(),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    label = { Text(scene.label()) },
                )
            }
        }
    }
}

// --- 4. Lighting (DESIGN.md §4) ---

@Composable
private fun LightingSection(lights: List<LightControl>, onToggleLight: (String) -> Unit) {
    SectionCard(title = "Lighting") {
        lights.forEachIndexed { index, light ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = if (light.isOn) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        light.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Switch(checked = light.isOn, onCheckedChange = { onToggleLight(light.id) })
            }
        }
    }
}

// --- 5. Privacy & View (DESIGN.md §5) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyAndViewSection(blinds: BlindPosition, onSetBlinds: (BlindPosition) -> Unit) {
    SectionCard(title = "Privacy & View") {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val positions = BlindPosition.entries
            positions.forEachIndexed { index, position ->
                SegmentedButton(
                    selected = blinds == position,
                    onClick = { onSetBlinds(position) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = positions.size),
                    label = { Text(position.name.label()) },
                )
            }
        }
        SunriseVista()
    }
}

/**
 * Placeholder for the "Sunrise Vista" room view (DESIGN.md §5 / Asset Preservation): a warm
 * sunrise-toned gradient with a centered overlay, standing in until the real image ships.
 */
@Composable
private fun SunriseVista() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.linearGradient(SunriseColors),
                shape = MaterialTheme.shapes.medium,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sunrise Vista",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

// --- 6. Bottom Navigation (DESIGN.md §6) ---

@Composable
private fun RoomBottomNav() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Filled.MeetingRoom, contentDescription = null) },
            label = { Text("Room") },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Outlined.RoomService, contentDescription = null) },
            label = { Text("Services") },
            colors = itemColors,
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Outlined.VpnKey, contentDescription = null) },
            label = { Text("Keys") },
            colors = itemColors,
        )
    }
}

// --- Shared building blocks ---

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(INNER_PADDING),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
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

private fun EnergyScene.icon(): ImageVector =
    when (this) {
        EnergyScene.WELCOME -> Icons.Filled.WbSunny // Day
        EnergyScene.AWAY -> Icons.Filled.AutoAwesome // Auto
        EnergyScene.SLEEP -> Icons.Filled.DarkMode // Night
    }

private fun String.label(): String = lowercase().replaceFirstChar { it.uppercase() }

private val SyncGreen = Color(0xFF3DDC84)
private val SunriseColors = listOf(
    Color(0xFFFFB36B),
    Color(0xFFFF8C66),
    Color(0xFF8E5A9E),
)

private val OUTER_MARGIN = 16.dp
private val CARD_GAP = 16.dp
private val INNER_PADDING = 20.dp
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
                        LightControl(id = "ceiling", name = "Main Lights", isOn = true),
                        LightControl(id = "bedside", name = "Bedside Lamps", isOn = false),
                        LightControl(id = "desk", name = "Desk Workspace", isOn = false),
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
