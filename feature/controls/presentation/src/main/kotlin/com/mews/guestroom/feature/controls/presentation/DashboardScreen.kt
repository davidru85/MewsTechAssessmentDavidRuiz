package com.mews.guestroom.feature.controls.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val onSetClimateMode: (ClimateMode) -> Unit,
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
            onSetClimateMode = viewModel::onSetClimateMode,
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
        ClimateSection(state.controls.climate, actions.onTargetTemperatureChange, actions.onSetClimateMode)
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
private fun ClimateSection(
    climate: Climate,
    onTargetTemperatureChange: (Int) -> Unit,
    onSetClimateMode: (ClimateMode) -> Unit,
) {
    SectionCard(title = "Climate Control") {
        var dialValue by remember(climate.targetCelsius) {
            mutableFloatStateOf(climate.targetCelsius.toFloat())
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ClimateDial(
                value = dialValue,
                currentCelsius = climate.currentCelsius,
                onValueChange = { dialValue = it },
                onValueChangeFinished = { onTargetTemperatureChange(dialValue.roundToInt()) },
            )
        }

        ClimateModeButtons(climate.mode, onSetClimateMode)
    }
}

@Composable
private fun ClimateDial(
    value: Float,
    currentCelsius: Double,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = ((value - MIN_TEMP) / (MAX_TEMP - MIN_TEMP)).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .size(240.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val outlineVariant = MaterialTheme.colorScheme.outlineVariant
        val backgroundColor = MaterialTheme.colorScheme.background
        val isDark = isSystemInDarkTheme()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    val updateValue: (Offset) -> Unit = { position ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = position.x - center.x
                        val dy = position.y - center.y
                        val angleRad = kotlin.math.atan2(dy, dx)
                        var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
                        // Adjust so top (12 o'clock) is 0 degrees, sweeping clockwise
                        var adjustedAngle = angleDeg + 90f
                        if (adjustedAngle < 0f) adjustedAngle += 360f
                        val newFraction = (adjustedAngle / 360f).coerceIn(0f, 1f)
                        val newValue = MIN_TEMP + newFraction * (MAX_TEMP - MIN_TEMP)
                        onValueChange(newValue.coerceIn(MIN_TEMP, MAX_TEMP))
                    }

                    detectDragGestures(
                        onDragStart = { offset ->
                            updateValue(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            updateValue(change.position)
                        },
                        onDragEnd = {
                            onValueChangeFinished()
                        },
                        onDragCancel = {
                            onValueChangeFinished()
                        },
                    )
                },
        ) {
            drawDial(fraction, primaryColor, outlineVariant, backgroundColor, isDark)
        }

        ClimateDialLabel(value = value, currentCelsius = currentCelsius)
    }
}

@Composable
private fun ClimateDialLabel(value: Float, currentCelsius: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Inside Temp",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${currentCelsius.roundToInt()}°C",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        ) {
            Text(
                text = "Target ${value.roundToInt()}°C",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}

private fun DrawScope.drawDial(
    fraction: Float,
    primaryColor: Color,
    outlineVariant: Color,
    backgroundColor: Color,
    isDark: Boolean,
) {
    val strokeWidth = 8.dp.toPx()
    val innerRadius = (size.minDimension - strokeWidth) / 2f

    // Draw background circle
    drawCircle(
        color = if (isDark) Color.White.copy(alpha = 0.05f) else outlineVariant.copy(alpha = 0.2f),
        radius = innerRadius,
        style = Stroke(width = strokeWidth),
    )

    // Draw active arc (from 12 o'clock, which is -90 degrees, sweeping clockwise)
    drawArc(
        color = primaryColor,
        startAngle = -90f,
        sweepAngle = fraction * 360f,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )

    // Draw thumb handle at the end of the active arc
    val angleRad = Math.toRadians((fraction * 360f - 90f).toDouble())
    val thumbX = (center.x + innerRadius * kotlin.math.cos(angleRad)).toFloat()
    val thumbY = (center.y + innerRadius * kotlin.math.sin(angleRad)).toFloat()

    if (isDark) {
        // Dark mode: filled primary thumb with border/glow representation
        drawCircle(color = primaryColor, center = Offset(thumbX, thumbY), radius = 12.dp.toPx())
        drawCircle(color = backgroundColor, center = Offset(thumbX, thumbY), radius = 8.dp.toPx())
        drawCircle(color = primaryColor, center = Offset(thumbX, thumbY), radius = 4.dp.toPx())
    } else {
        // Light mode: white thumb with primary border
        drawCircle(color = primaryColor, center = Offset(thumbX, thumbY), radius = 12.dp.toPx())
        drawCircle(color = Color.White, center = Offset(thumbX, thumbY), radius = 9.dp.toPx())
    }
}

@Composable
private fun ClimateModeButtons(mode: ClimateMode, onSetClimateMode: (ClimateMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        // The two buttons match DESIGN.md §2; tapping one sets that mode (the active
        // button reflects the live mode and stays high-filled while selected).
        ModeButton(
            label = "Auto Fan",
            icon = Icons.Filled.Air,
            active = mode == ClimateMode.AUTO,
            onClick = { onSetClimateMode(ClimateMode.AUTO) },
            modifier = Modifier.weight(1f),
        )
        ModeButton(
            label = "Cooling",
            icon = Icons.Filled.AcUnit,
            active = mode == ClimateMode.COOL,
            onClick = { onSetClimateMode(ClimateMode.COOL) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ModeButton(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content = @Composable {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
    if (active) {
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(min = 52.dp),
            shape = MaterialTheme.shapes.medium,
        ) { content() }
    } else {
        OutlinedButton(
            onClick = onClick,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (light.isFaulty) {
                        // Surface the deliberate fault up-front; the switch is disabled
                        // because the device is known to be unreachable (DESIGN.md §4).
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Unreachable",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Switch(
                        checked = light.isOn,
                        onCheckedChange = { onToggleLight(light.id) },
                        enabled = !light.isFaulty,
                    )
                }
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
            .clip(MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = com.mews.guestroom.core.ui.R.drawable.sunrise_vista),
            contentDescription = "Sunrise Vista",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                    ),
                ),
        )
        Text(
            text = "Sunrise Vista",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center,
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
            actions = DashboardActions({}, {}, {}, {}, {}),
        )
    }
}
