# Smart Guest Room Management — Android Project Structure Blueprint

> Foundational, high-level directory structure for the **Smart Guest Room Management**
> Android prototype (Kotlin, Jetpack Compose, Clean Architecture + MVVM).
>
> This blueprint is derived from the project documents in this repository
> (`Product_Builder_Mobile_Android_Take_Home_Task.md`,
> `Smart-Guest-Room-Management.md`, and `specifications/spec-01` … `spec-08`)
> and is intended to be the single source of truth for the initial Android Studio
> project layout.

---

## 1. Architectural Foundation

| Decision | Choice | Rationale (from docs) |
|---|---|---|
| Language | Kotlin 1.9+, JDK 17, AGP 8+ | `spec-04` — "Kotlin 1.9+, JDK 17, AGP 8+" |
| Build system | Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`) | `spec-04` |
| UI toolkit | Jetpack Compose + Material 3 | `spec-04`, `spec-07` |
| Architecture | Clean Architecture (Data / Domain / Presentation) + MVVM | `spec-04` |
| State model | Coroutines + `StateFlow` / `SharedFlow` | `spec-04` |
| Navigation | `androidx.navigation:navigation-compose`, type-safe (Kotlin Serialization) | `spec-04` |
| DI | Hilt with `@Binds` + qualifier-based mock/live swap | `spec-04`, `spec-05` |
| Reactive simulation | In-process MQTT-style pub/sub coroutine emitter | `spec-04` |
| Testing | JUnit5, MockK, Turbine, Paparazzi/Roborazzi, Compose UI tests | `spec-04` |
| Static analysis | ktlint, detekt (custom rules for layer purity) | `spec-04`, `spec-07` |
| Build variants | `mock` (in-memory) and `live` (MQTT-backed) flavors, gated by `BuildConfig.USE_MOCK_DATA` | `spec-05` |
| Package root | `com.mews.guestroom` | Aligns with Mews hospitality context |

The product scope (from `spec-03`) is mapped to feature modules:

- **Room Controls & Automation** → `:feature:dashboard`, `:feature:controls`
- **Security & Access Control** → `:feature:access`
- **Notifications & Alerts** → `:feature:notifications`
- **Chat & Hotel Services** → `:feature:services`
- **General Info & Profile** → `:feature:info`, `:feature:profile`

---

## 2. Top-Level Layout

```
SmartGuestRoom/                            # Android Studio project root
├── app/                                   # :app — application module, composition root
├── core/                                  # Shared infrastructure modules
│   ├── common/
│   ├── domain/                            # Pure-Kotlin business rules (NO Android imports)
│   ├── data/                              # Repository implementations + mock / MQTT data sources
│   ├── network/                           # MQTT + future REST/PMS connectivity
│   ├── ui/                                # Design tokens, Material 3 theme, shared Composables
│   └── testing/                           # Shared test utilities, fakes, rules
├── feature/                               # User-facing feature modules
│   ├── dashboard/
│   ├── controls/
│   ├── access/
│   ├── services/
│   ├── notifications/
│   ├── profile/
│   └── info/
├── build-logic/                           # Gradle convention plugins (shared build config)
├── gradle/                                # Wrapper, version catalog, static-analysis config
├── docs/                                  # ADRs, architecture notes, design tokens, migration runbook
├── scripts/                               # Local dev helpers (bootstrap, test runners)
├── .github/                               # CI workflows
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat
├── .editorconfig
├── .gitattributes
├── .gitignore
├── README.md
└── LICENSE
```

---

## 3. Module Dependency Graph

Strict layering — arrows point from dependent to dependency.

```
                          :app
                            │
       ┌────────────────────┼─────────────────────┐
       │                    │                     │
   :feature:*           :core:ui            :core:testing
       │                    │                     │
       ├────────────► :core:domain ◄──────────────┘
       │                    ▲
       │            ┌───────┴───────┐
       │       :core:data      :core:network
       │            │               │
       │            └────► :core:common ◄────┘
       │                            ▲
       └────────────► :core:ui ────┘
                                    
:feature:*   → :core:domain (only) by default
:core:data   → :core:domain, :core:common, :core:network
:core:ui     → :core:common
:app         → every module
```

**Enforcement:** detekt custom rule rejects `android.*` imports inside `:core:domain`
(verifies the "100% architecture layer purity" KPI from `spec-04`).

---

## 4. Detailed Directory Tree

```
SmartGuestRoom/
│
├── app/                                                   # :app
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/mews/guestroom/
│       │   │   ├── GuestRoomApplication.kt               # @HiltAndroidApp; Timber init; logger
│       │   │   ├── MainActivity.kt                        # Single Activity, hosts Compose NavHost
│       │   │   ├── di/
│       │   │   │   ├── AppModule.kt                       # App-wide @Module (logger, clock)
│       │   │   │   ├── DataSourceModule.kt                # Picks mock vs. mqtt via BuildConfig.USE_MOCK_DATA
│       │   │   │   └── DispatcherModule.kt                # @IoDispatcher / @DefaultDispatcher / @MainDispatcher
│       │   │   └── navigation/
│       │   │       ├── AppNavGraph.kt                     # NavHost root
│       │   │       └── Destinations.kt                    # @Serializable type-safe destinations
│       │   └── res/
│       │       ├── values/
│       │       │   ├── colors.xml
│       │       │   ├── strings.xml                        # Source-of-truth English strings
│       │       │   ├── themes.xml                         # M3 light theme stub (real theming in Compose)
│       │       │   └── styles.xml
│       │       ├── values-night/
│       │       │   └── themes.xml                         # Dark-mode override
│       │       ├── drawable/
│       │       │   └── ic_launcher_foreground.xml
│       │       ├── mipmap-anydpi-v26/
│       │       │   ├── ic_launcher.xml
│       │       │   └── ic_launcher_round.xml
│       │       ├── mipmap-hdpi/
│       │       ├── mipmap-mdpi/
│       │       ├── mipmap-xhdpi/
│       │       ├── mipmap-xxhdpi/
│       │       ├── mipmap-xxxhdpi/
│       │       └── xml/
│       │           └── network_security_config.xml       # TLS pinning for prod MQTT
│       ├── mock/                                          # `mock` flavor source set
│       │   └── java/com/mews/guestroom/di/
│       │       └── MockDataSourceModule.kt                # @Binds MockRoomStateDataSource
│       ├── live/                                          # `live` flavor source set
│       │   └── java/com/mews/guestroom/di/
│       │       ├── MqttDataSourceModule.kt                # @Binds MqttRoomStateDataSource
│       │       └── MqttConfigModule.kt                    # broker URL, credentials, TLS
│       └── test/
│           └── java/com/mews/guestroom/
│               └── SmokeTest.kt
│
├── core/
│   │
│   ├── common/                                            # :core:common
│   │   ├── build.gradle.kts                               # kotlin("jvm") — no Android, no Hilt
│   │   └── src/
│   │       └── main/kotlin/com/mews/guestroom/core/common/
│   │           ├── di/
│   │           │   └── DispatcherQualifiers.kt            # @IoDispatcher, @DefaultDispatcher, @MainDispatcher
│   │           ├── result/
│   │           │   ├── DataResult.kt                      # sealed Success<T> / Error / Loading
│   │           │   └── ErrorType.kt                       # Network, NotFound, Auth, Timeout, Unknown
│   │           ├── coroutines/
│   │           │   └── DispatcherProvider.kt              # interface + DefaultDispatcherProvider
│   │           ├── time/
│   │           │   └── Clock.kt                           # injectable Clock (testability)
│   │           ├── logging/
│   │           │   ├── Logger.kt                          # interface
│   │           │   └── TimberLogger.kt                    # prod impl
│   │           └── ext/
│   │               ├── FlowExt.kt                         # debounce, throttle, combineLatest, onEachTimed
│   │               └── DurationExt.kt
│   │
│   ├── domain/                                            # :core:domain  ★ PURE KOTLIN — NO ANDROID
│   │   ├── build.gradle.kts                               # java-library + kotlin("jvm") only
│   │   └── src/
│   │       ├── main/kotlin/com/mews/guestroom/core/domain/
│   │       │   ├── model/
│   │       │   │   ├── RoomState.kt                       # Aggregate state value type
│   │       │   │   ├── ClimateState.kt
│   │       │   │   ├── LightingState.kt
│   │       │   │   ├── BlindState.kt
│   │       │   │   ├── LockState.kt
│   │       │   │   ├── EnergyMode.kt                      # sealed: Sleep, Away, Welcome, WakeUp, None
│   │       │   │   ├── Occupancy.kt                       # sealed: CheckedIn, CheckedOut, Maintenance, Dnd
│   │       │   │   ├── Temperature.kt                     # value class
│   │       │   │   ├── Lumens.kt                          # value class
│   │       │   │   ├── Brightness.kt                      # value class (0..1)
│   │       │   │   └── ReservationWindow.kt               # check-in / check-out timestamps
│   │       │   ├── repository/                            # Interfaces only — data layer implements
│   │       │   │   ├── RoomStateRepository.kt
│   │       │   │   ├── DeviceControlRepository.kt
│   │       │   │   ├── ServiceRequestRepository.kt
│   │       │   │   ├── ReservationRepository.kt
│   │       │   │   └── NotificationRepository.kt
│   │       │   └── usecase/                               # One use case per user action
│   │       │       ├── ObserveRoomStateUseCase.kt
│   │       │       ├── ToggleLightUseCase.kt
│   │       │       ├── SetLightBrightnessUseCase.kt
│   │       │       ├── SetTemperatureUseCase.kt
│   │       │       ├── SetBlindsUseCase.kt
│   │       │       ├── ActivateEnergyModeUseCase.kt
│   │       │       ├── UnlockDoorUseCase.kt
│   │       │       ├── RequestAmenityUseCase.kt
│   │       │       ├── RequestLateCheckoutUseCase.kt
│   │       │       ├── ObserveReservationsUseCase.kt
│   │       │       └── GetHotelFacilitiesUseCase.kt
│   │       └── test/kotlin/com/mews/guestroom/core/domain/
│   │           ├── usecase/
│   │           │   ├── ToggleLightUseCaseTest.kt
│   │           │   ├── SetTemperatureUseCaseTest.kt
│   │           │   ├── ActivateEnergyModeUseCaseTest.kt
│   │           │   └── UnlockDoorUseCaseTest.kt
│   │           └── repository/
│   │               └── FakeRoomStateRepository.kt          # Shared fake for use-case tests
│   │
│   ├── data/                                              # :core:data
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       └── main/java/com/mews/guestroom/core/data/
│   │           ├── repository/                             # Implements :core:domain interfaces
│   │           │   ├── RoomStateRepositoryImpl.kt
│   │           │   ├── DeviceControlRepositoryImpl.kt
│   │           │   ├── ServiceRequestRepositoryImpl.kt
│   │           │   ├── ReservationRepositoryImpl.kt
│   │           │   └── NotificationRepositoryImpl.kt
│   │           ├── source/                                # Data-source interfaces + impls
│   │           │   ├── RoomStateDataSource.kt             # interface
│   │           │   ├── DeviceCommandDataSource.kt         # interface
│   │           │   ├── ServiceRequestDataSource.kt        # interface
│   │           │   ├── ReservationDataSource.kt           # interface
│   │           │   ├── mock/                              # Prototype (in-process pub/sub)
│   │           │   │   ├── MockRoomStateDataSource.kt          # @Singleton, MutableStateFlow
│   │           │   │   ├── MockDeviceCommandDataSource.kt
│   │           │   │   ├── MockServiceRequestDataSource.kt
│   │           │   │   ├── MockReservationDataSource.kt
│   │           │   │   └── StateSimulationEngine.kt             # Background coroutine, drifts temperature
│   │           │   └── mqtt/                              # Production (filled in for real deployment)
│   │           │       ├── MqttRoomStateDataSource.kt          # Paho/HiveMQ wrapper
│   │           │       ├── MqttDeviceCommandDataSource.kt
│   │           │       ├── MqttReservationDataSource.kt
│   │           │       └── MqttTopic.kt                       # hotels/{id}/rooms/{id}/state, /cmd
│   │           ├── mapper/                                # DTO ↔ domain mappers
│   │           │   ├── RoomStateMapper.kt
│   │           │   ├── ClimateMapper.kt
│   │           │   └── ServiceRequestMapper.kt
│   │           ├── dto/                                   # Wire / mock-DTO shapes
│   │           │   ├── RoomStateDto.kt
│   │           │   ├── ClimateDto.kt
│   │           │   ├── LightingDto.kt
│   │           │   └── ServiceRequestDto.kt
│   │           ├── di/
│   │           │   ├── DataModule.kt                       # @Binds repos
│   │           │   └── CoroutineScopeModule.kt             # @ApplicationScope / @IoScope qualifiers
│   │           └── asset/
│   │               ├── mock-room-config.json                # Bundled mock fixtures
│   │               ├── mock-hotel-info.json
│   │               └── mock-reservation.json
│   │
│   ├── network/                                           # :core:network
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/core/network/
│   │       ├── mqtt/
│   │       │   ├── MqttClientFactory.kt
│   │       │   ├── MqttConfig.kt
│   │       │   ├── MqttCredentialsProvider.kt
│   │       │   └── ReconnectStrategy.kt                   # Exponential backoff
│   │       ├── ssl/
│   │       │   └── CertificatePinner.kt
│   │       └── di/
│   │           └── NetworkModule.kt
│   │
│   ├── ui/                                                # :core:ui
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/core/ui/
│   │       ├── theme/
│   │       │   ├── Color.kt                                # M3 ColorScheme (light + dark + dynamic)
│   │       │   ├── Type.kt                                 # Typography tokens
│   │       │   ├── Shape.kt                                # Shape tokens
│   │       │   ├── Spacing.kt                              # 4dp grid tokens
│   │       │   └── GuestRoomTheme.kt                       # @Composable wrapper, dynamicColor support
│   │       ├── component/                                  # Reusable M3-first Composables
│   │       │   ├── GlowCard.kt
│   │       │   ├── PrimarySlider.kt
│   │       │   ├── ToggleChip.kt
│   │       │   ├── StatusPill.kt
│   │       │   ├── SectionHeader.kt
│   │       │   └── EmptyState.kt
│   │       ├── icon/
│   │       │   └── GuestRoomIcons.kt                       # Central icon set (Material + brand)
│   │       ├── preview/
│   │       │   ├── PreviewAnnotation.kt                    # @PreviewLightDark, @PreviewScreenSizes
│   │       │   └── PreviewContainer.kt
│   │       └── ext/
│   │           ├── ModifierExt.kt                          # Semantics, padding, click-target sizing
│   │           └── ContentDescriptionExt.kt                # Accessibility helpers
│   │
│   └── testing/                                           # :core:testing
│       ├── build.gradle.kts
│       └── src/main/java/com/mews/guestroom/core/testing/
│           ├── coroutine/
│           │   ├── MainDispatcherRule.kt                   # JUnit4 rule (also for JUnit5 via extension)
│           │   ├── TestScopeRule.kt
│           │   └── TurbineExt.kt
│           ├── turbine/
│           │   └── FlowTestUtils.kt
│           ├── fake/
│           │   ├── FakeClock.kt
│           │   ├── FakeLogger.kt
│           │   ├── FakeRoomStateDataSource.kt
│           │   └── FakeReservationRepository.kt
│           └── compose/
│               ├── GuestRoomComposeTestRule.kt
│               └── Robot.kt                                # Screen-object pattern helpers
│
├── feature/
│   │
│   ├── dashboard/                                          # :feature:dashboard
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/feature/dashboard/
│   │       ├── DashboardScreen.kt                          # Entry Composable
│   │       ├── DashboardViewModel.kt                       # Hilt-injected, observes ObserveRoomStateUseCase
│   │       ├── DashboardUiState.kt                         # sealed UiState (Loading / Content / Error)
│   │       ├── DashboardEvent.kt                           # one-shot events (toast, nav)
│   │       ├── component/
│   │       │   ├── RoomStateHeroCard.kt
│   │       │   ├── EnergyModeRow.kt
│   │       │   ├── QuickActionsGrid.kt
│   │       │   └── ThermostatDial.kt                       # Highlight component for the demo
│   │       └── navigation/
│   │           └── DashboardDestination.kt
│   │
│   ├── controls/                                           # :feature:controls
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/feature/controls/
│   │       ├── climate/
│   │       │   ├── ClimateScreen.kt
│   │       │   ├── ClimateViewModel.kt
│   │       │   └── ClimateUiState.kt
│   │       ├── lighting/
│   │       │   ├── LightingScreen.kt
│   │       │   ├── LightingViewModel.kt
│   │       │   ├── LightingUiState.kt
│   │       │   └── component/LightSceneCard.kt
│   │       ├── blinds/
│   │       │   ├── BlindsScreen.kt
│   │       │   ├── BlindsViewModel.kt
│   │       │   └── BlindsUiState.kt
│   │       └── navigation/
│   │           └── ControlsGraph.kt                        # Nested NavGraph for the 3 sub-screens
│   │
│   ├── access/                                             # :feature:access (security & entry)
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/feature/access/
│   │       ├── AccessScreen.kt
│   │       ├── AccessViewModel.kt
│   │       ├── StaffLoginScreen.kt
│   │       ├── component/
│   │       │   ├── DoorStatusCard.kt
│   │       │   ├── UnlockAnimation.kt
│   │       │   └── AuditLogList.kt
│   │       └── navigation/
│   │           └── AccessDestination.kt
│   │
│   ├── services/                                           # :feature:services (chat & hotel services)
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/feature/services/
│   │       ├── ServicesScreen.kt
│   │       ├── ServicesViewModel.kt
│   │       ├── chat/
│   │       │   ├── ChatScreen.kt
│   │       │   ├── ChatViewModel.kt
│   │       │   └── component/MessageBubble.kt
│   │       ├── request/
│   │       │   ├── QuickRequestSheet.kt
│   │       │   ├── RequestStatusList.kt
│   │       │   └── component/ServiceRequestCard.kt
│   │       ├── addon/
│   │       │   ├── AddOnBrowserScreen.kt
│   │       │   └── AddOnViewModel.kt
│   │       └── navigation/
│   │           └── ServicesDestination.kt
│   │
│   ├── notifications/                                      # :feature:notifications
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/feature/notifications/
│   │       ├── NotificationsScreen.kt
│   │       ├── NotificationsViewModel.kt
│   │       ├── component/
│   │       │   ├── NotificationItem.kt
│   │       │   └── CheckOutReminderCard.kt
│   │       └── navigation/
│   │           └── NotificationsDestination.kt
│   │
│   ├── profile/                                            # :feature:profile
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mews/guestroom/feature/profile/
│   │       ├── ProfileScreen.kt
│   │       ├── ProfileViewModel.kt
│   │       ├── ProfileUiState.kt
│   │       ├── preferences/
│   │       │   ├── AccessibilityPreferencesScreen.kt
│   │       │   └── PreferencesViewModel.kt
│   │       └── navigation/
│   │           └── ProfileDestination.kt
│   │
│   └── info/                                               # :feature:info (hotel facilities, FAQs)
│       ├── build.gradle.kts
│       └── src/main/java/com/mews/guestroom/feature/info/
│           ├── InfoScreen.kt
│           ├── InfoViewModel.kt
│           ├── component/
│           │   ├── FacilityCard.kt
│           │   └── FacilityHoursRow.kt
│           └── navigation/
│               └── InfoDestination.kt
│
├── build-logic/                                            # Gradle convention plugins
│   ├── settings.gradle.kts
│   └── src/main/kotlin/
│       ├── AndroidAppConventionPlugin.kt
│       ├── AndroidLibraryConventionPlugin.kt
│       ├── AndroidFeatureConventionPlugin.kt                # Compose + Hilt + Navigation
│       ├── AndroidComposeConventionPlugin.kt
│       ├── AndroidHiltConventionPlugin.kt
│       ├── AndroidRoomConventionPlugin.kt                   # Optional persistence layer
│       ├── KotlinLibraryConventionPlugin.kt                 # For :core:domain (pure JVM)
│       ├── JvmTestConventionPlugin.kt
│       ├── AndroidTestConventionPlugin.kt
│       └── DetektConventionPlugin.kt
│
├── gradle/
│   ├── libs.versions.toml                                   # Version Catalog — single source of truth
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── detekt/
│       └── detekt.yml                                       # detekt config; custom rule for layer purity
│
├── docs/
│   ├── architecture/
│   │   ├── 01-clean-architecture.md
│   │   ├── 02-state-management.md
│   │   ├── 03-navigation.md
│   │   └── 04-mock-vs-live.md
│   ├── decisions/                                           # Architecture Decision Records
│   │   ├── adr-template.md
│   │   ├── 0001-multi-module-clean-architecture.md
│   │   ├── 0002-hilt-over-koin.md
│   │   ├── 0003-stateflow-vs-sharedflow.md
│   │   ├── 0004-mqtt-topic-layout.md
│   │   ├── 0005-cut-tv-control-from-mvp.md                  # Evidence of MoSCoW/ICE cuts
│   │   ├── 0006-compose-navigation-type-safe-routes.md
│   │   └── 0007-dark-mode-parity-policy.md
│   ├── design/
│   │   ├── design-tokens.md                                 # Color/typography/shape tokens
│   │   ├── component-catalog.md                             # Audit trail for M3 coverage
│   │   └── accessibility-baseline.md                        # 48dp / WCAG 2.1 AA / TalkBack
│   ├── adr-template.md
│   ├── prototype-to-production.md                            # Migration runbook (from spec-05)
│   └── demo-script.md                                       # 60–90s scripted reviewer demo
│
├── scripts/
│   ├── bootstrap.sh                                         # Clone + first build (idempotent)
│   ├── run-unit-tests.sh
│   ├── run-ui-tests.sh
│   └── run-detekt.sh
│
├── .github/
│   └── workflows/
│       ├── ci.yml                                           # Lint + unit + ui tests + assembleDebug
│       └── codeql-analysis.yml
│
├── .editorconfig
├── .gitattributes
├── .gitignore
├── .detekt.yml
├── README.md
├── LICENSE
├── settings.gradle.kts                                       # includes all modules; enables version catalog
├── build.gradle.kts                                          # root build script
├── gradle.properties                                         # JVM args, AndroidX, parallel build flags
└── gradlew / gradlew.bat
```

---

## 5. Module-by-Module Rationale

### 5.1 `:app`
The composition root. Owns the single `MainActivity` hosting the Compose `NavHost`,
the `@HiltAndroidApp` `Application` class, and the **DI modules that bind the
data-source implementation** (`MockRoomStateDataSource` for the `mock` flavor vs.
`MqttRoomStateDataSource` for the `live` flavor) — directly implementing the
"swap data source in <1 hour" KPI from `spec-05`.

### 5.2 `:core:domain`
The heart of Clean Architecture. **Zero Android imports** — uses `kotlin("jvm")`
only. Holds immutable value types (`RoomState`, `EnergyMode`, `Temperature`),
repository *interfaces*, and one use case per user-facing action
(`ToggleLightUseCase`, `ActivateSleepModeUseCase`, etc., as called out in
`spec-04`). This module is the single source of truth for business rules and
is what production deployments reuse verbatim.

### 5.3 `:core:data`
Implements the domain repositories. The `source/` package is split into
`source/mock/` (in-process `MutableStateFlow` + `StateSimulationEngine` that
drifts temperature on a timer, exactly as `spec-04` describes) and
`source/mqtt/` (Paho/HiveMQ-shaped stubs ready to be wired to a real broker
in production). This is the **only** layer that differs between prototype
and production — preserving the "0 lines of UI/domain code change between
modes" KPI from `spec-05`.

### 5.4 `:core:network`
Houses the MQTT client factory, TLS pinning, reconnect strategy, and topic
builder. Kept separate from `:core:data` so the data layer stays
broker-agnostic — repos see `RoomStateDataSource`, not `MqttClient`.

### 5.5 `:core:ui`
Owns the **entire design system**: Material 3 color schemes (light + dark +
dynamic/Material You), typography, shape, spacing, and a catalog of
reusable Composables. Every feature module depends on this — guaranteeing
the ">85% M3 components" and "100% dark mode parity" KPIs from `spec-07`.

### 5.6 `:core:common`
Cross-cutting utilities that *cannot* live in `:core:domain` (they touch
`kotlinx.coroutines`/`Flow`/logging) but must be Android-free or Android-light.
Includes dispatcher qualifiers, `DataResult<T>`, an injectable `Clock`,
and the `Logger` interface.

### 5.7 `:core:testing`
Shared test fixtures: `MainDispatcherRule`, `TurbineExt`, fakes for
`Clock`/`Logger`/data sources, and a `Robot` base class for the
screen-object pattern in Compose UI tests.

### 5.8 Feature modules
One Gradle module per user-facing surface. Each:

- depends on `:core:domain` (use cases) and `:core:ui` (design system);
- owns a `*ViewModel` exposing `StateFlow<UiState>`;
- owns a `*Screen` Composable that collects with
  `collectAsStateWithLifecycle()`;
- ships its own `*Destination` for type-safe Compose Navigation;
- keeps UI state in a sealed `UiState` class for exhaustive `when` branches.

This layout supports the demo flow from `spec-04`:
`Dashboard → toggle light → activate Sleep Mode → unlock door`, each step
crossing exactly one feature module.

### 5.9 `:build-logic` (convention plugins)
Avoids duplicating Gradle config across 12+ modules. A single
`AndroidFeatureConventionPlugin` is applied per feature module and pulls in
Compose, Hilt, Navigation, Coroutines, and the standard test dependencies.
A custom detekt rule here enforces the "100% architecture layer purity"
KPI by banning `android.*` imports from `:core:domain`.

### 5.10 `:docs`
- `architecture/` — narrative explanations of the four core mechanics.
- `decisions/` — ADRs documenting *why* a choice was made; supports
  the "decision log" requirement in `spec-07` and the AI-First traceability
  requirement in `spec-08`.
- `design/` — design tokens and the M3 component audit log
  (proves the M3 coverage KPI).
- `prototype-to-production.md` — the **migration runbook** from `spec-05`:
  exactly which files change when swapping from `mock` to `live`, and
  the checklist for production hardening (auth, TLS, telemetry, crash
  reporting, pen-test).
- `demo-script.md` — the scripted 60–90s demo path required by `spec-04`.

---

## 6. Key Libraries (declared in `gradle/libs.versions.toml`)

| Concern | Library |
|---|---|
| UI | `androidx.compose:compose-bom`, `androidx.compose.material3`, `androidx.activity:activity-compose` |
| Navigation | `androidx.navigation:navigation-compose` (type-safe routes, Kotlin 1.9+) |
| State | `kotlinx-coroutines-core`, `kotlinx-coroutines-android` |
| DI | `com.google.dagger:hilt-android`, `androidx.hilt:hilt-navigation-compose` |
| Serialization | `kotlinx-serialization-json` |
| Networking | `org.eclipse.paho:org.eclipse.paho.android.service` *(or HiveMQ client)* |
| Date/Time | `kotlinx-datetime` |
| Testing (unit) | JUnit5, MockK, Turbine, `kotlinx-coroutines-test` |
| Testing (UI) | `androidx.compose.ui:ui-test-junit4` |
| Snapshot testing | `app.cash.paparazzi:paparazzi` or `io.github.takahirom.roborazzi` |
| Static analysis | `com.google.devtools.ksp`, `io.gitlab.arturbosch.detekt`, `com.pinterest.ktlint` |
| Linting | `androidx.lint:lint-checks` |
| Performance | `androidx.benchmark:benchmark-macro-junit4` |
| Logging | `com.jakewharton.timber:timber` |
| Build | AGP 8+, Gradle 8+, Kotlin 1.9+, JDK 17 |

---

## 7. How the Structure Maps to the Specs

| Spec requirement | Structural answer |
|---|---|
| Clean Architecture + MVVM (`spec-04`) | `:core:domain` (pure Kotlin) ← `:core:data` (impls) → `:feature:*` (ViewModels) → `:core:ui` (Composables) |
| `StateFlow` / `SharedFlow` (`spec-04`) | All repositories expose `Flow<...>` in `:core:domain`; ViewModels expose `StateFlow<UiState>` and `SharedFlow<Event>` |
| Hilt-based DI with mode swap (`spec-05`) | `MockDataSourceModule` in `src/mock/`, `MqttDataSourceModule` in `src/live/`, gated by `BuildConfig.USE_MOCK_DATA` |
| MQTT-style simulation (`spec-04`) | `MockRoomStateDataSource` + `StateSimulationEngine` in `:core:data/source/mock/` |
| Material 3 + dark mode + dynamic color (`spec-07`) | `:core:ui/theme/GuestRoomTheme.kt` + `values-night/themes.xml` |
| One use case per action (`spec-04`) | 11 use cases listed in `:core:domain/usecase/` |
| Five feature pillars (`spec-03`) | `:feature:dashboard` + `:feature:controls` + `:feature:access` + `:feature:services` + `:feature:notifications` + `:feature:profile` + `:feature:info` |
| Test discipline (`spec-04`) | `:core:testing` + per-module `test/` source sets; `Turbine` for Flow; `MockK` for repos |
| Static analysis + layer purity (`spec-04`, `spec-07`) | `:build-logic/DetektConventionPlugin.kt` + `gradle/detekt/detekt.yml` |
| Version catalog (`spec-04`) | `gradle/libs.versions.toml` |
| M3 component audit, decision log, migration runbook (`spec-07`, `spec-05`) | `docs/design/`, `docs/decisions/`, `docs/prototype-to-production.md` |
| AI-First traceability (`spec-08`) | AI-assisted scaffolding called out in `docs/decisions/`; `@generated` markers in scaffolded files |

---

## 8. Naming Conventions

- **Packages** — lowercase, dot-separated: `com.mews.guestroom.<layer>.<feature>`
- **Classes** — `PascalCase`; ViewModels end in `ViewModel`; Use cases end in
  `UseCase`; Composables end in `Screen` (top-level) or are noun-only
  (sub-components: `ThermostatDial`, not `ThermostatDialComposable`).
- **Functions** — `camelCase`; Composables are `PascalCase`.
- **State** — `*UiState` (sealed), `*Event` (one-shot, `SharedFlow`),
  `*Action` (user intents).
- **Tests** — `*Test` (unit), `*ScreenTest` (UI); fake repositories
  prefixed `Fake`.
- **Resources** — `snake_case`; string keys `<feature>_<purpose>` (e.g.
  `dashboard_thermostat_label`).

---

## 9. Out-of-Scope (deliberately cut from MVP)

Per `spec-07` prioritization, the following are **not** present in the
blueprint and are documented as cuts in `docs/decisions/`:

- TV / entertainment control
- In-room tablet companion mode
- Voice assistant integration (hooks reserved as TODO comments in
  `:feature:controls`)
- Native iOS build (Android-only per task scope)
- Real PMS / payment integrations (mocked)
- Loyalty program deep-links
- Multi-property white-label theming (single brand for MVP)

---

## 10. Next Steps After Scaffolding

1. Apply convention plugins and verify `./gradlew help` passes.
2. Wire the version catalog and run `./gradlew :app:dependencies` to lock
   the dependency graph.
3. Generate the first round of unit tests for `:core:domain` use cases
   (Turbine-driven) — this is the fastest reviewer-facing signal of
   engineering craft.
4. Build the `Dashboard` happy path: live `RoomState` from the mock
   source, `ThermostatDial` Composable, `EnergyModeRow` with one-tap
   Sleep Mode.
5. Record the 60–90s demo path from `docs/demo-script.md`.
6. Open the first ADR documenting the `mock` / `live` flavor decision.
