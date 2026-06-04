# Detailed Specification for The Android Prototype: Architecture and Craft

## Section Overview (Executive Summary)

This section defines the technical foundation of the prototype: a Kotlin-based Android application built with Clean Architecture + MVVM, reactive state management via Kotlin Coroutines and Flow, and Jetpack Compose for the UI layer. The data layer simulates a real-time IoT messaging broker (MQTT-style) to demonstrate end-to-end reactivity without requiring live hardware. The goal is to ship a credible, demoable prototype that proves engineering craft and architectural maturity while remaining fast to iterate.

---

## Core Components & Features

- **Project Module Structure:** Multi-module Gradle setup — `:app`, `:core:ui`, `:core:domain`, `:core:data`, `:core:common`, `:feature:dashboard`, `:feature:controls`, etc. — enforcing Clean Architecture boundaries.
- **Domain Layer:** Pure-Kotlin use cases — `ToggleLightUseCase`, `SetTemperatureUseCase`, `ActivateSleepModeUseCase`, `UnlockDoorUseCase`, `SubscribeToRoomStateUseCase` — with no Android dependencies.
- **Data Layer:** Repository pattern with `RoomStateRepository`, `DeviceControlRepository`, etc. A `MockRoomStateDataSource` simulates an MQTT broker, emitting `StateFlow<RoomState>` updates on a configurable cadence.
- **State Simulation Engine:** Background coroutine that mutates room state over time (e.g., ambient temperature drifts toward set point, motion sensor fires on a timer) to demonstrate real-time UX behavior.
- **UI Layer (Jetpack Compose):** Dashboard screen with state-observing Composables; Material 3 components; Dark Mode; dynamic color (Material You) where supported.
- **Dependency Injection:** Hilt-based; bindings allow swapping the `MockRoomStateDataSource` for a real one without changing downstream code.
- **Reactive State Flow:** Use `StateFlow` for stable state (current temp, light status) and `SharedFlow` for one-shot events (door unlock confirmation, error toasts).
- **Use Case Pattern:** Each user-facing action wraps a single use case, keeping ViewModels thin and testable.
- **Test Surface:** Unit tests for use cases and ViewModels (Turbine for Flow), UI tests for critical Compose screens.
- **Demo Flow:** A scripted 60–90 second demo path that walks a reviewer through: open app → see live state → toggle light → activate Sleep Mode → unlock door.

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Hiring Manager / Tech Reviewer (primary audience for the prototype)
- **Pain:** Has seen dozens of "todo app" Android submissions; wants to see architectural thinking, not just a UI.
- **Pain:** Looks for Clean Architecture discipline, idiomatic Kotlin, proper concurrency, and test awareness.
- **Pain:** Wants to evaluate craft, not feature count — fewer features done well beats many features done shallowly.

### Engineering Team (if hired)
- **Pain:** Will inherit the codebase; needs it to be readable, modular, and easy to extend.

### Product / Demo Audience
- **Pain:** Wants the prototype to *feel* like a real product — responsive, polished, with believable state changes.

### Future Maintenance Developer
- **Pain:** Needs clear module boundaries and well-named abstractions to onboard quickly.

---

## Functional Requirements (FRs) / User Stories

1. **As a tech reviewer**, I want to see a layered Clean Architecture in the codebase, **so that** I can evaluate the candidate's structural discipline.
2. **As a tech reviewer**, I want the data layer to simulate real-time state changes (e.g., temperature drift), **so that** the prototype demonstrates reactive UX behavior convincingly.
3. **As a tech reviewer**, I want use cases to be pure Kotlin with no Android imports, **so that** I can verify the domain layer is portable and unit-testable.
4. **As a demo viewer**, I want the Dashboard to show live room state and respond instantly to controls, **so that** the prototype feels like a real product.
5. **As a future maintainer**, I want Hilt-based DI with clear module boundaries, **so that** swapping the mock data source for a real one is trivial.
6. **As a tech reviewer**, I want to see unit tests for at least the use cases and ViewModels, **so that** I can gauge test discipline.
7. **As a demo viewer**, I want a polished, Material 3-styled UI with dark mode, **so that** the prototype looks production-grade.
8. **As a tech reviewer**, I want to see idiomatic Kotlin (Coroutines, Flow, sealed classes, value classes), **so that** I can assess language fluency.
9. **As a demo viewer**, I want one-tap activation of energy modes (e.g., Sleep Mode), **so that** the multi-system orchestration is visible at a glance.
10. **As a tech reviewer**, I want the project to build and run on a fresh checkout with minimal setup, **so that** I can evaluate it without friction.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| Build success on clean clone | 100% | `./gradlew assembleDebug` |
| Time to first meaningful demo (cold start) | <3 seconds | Lint / manual timing |
| Code coverage on domain layer | >80% | JaCoCo report |
| UI freeze / ANR during demo | 0 | StrictMode, manual observation |
| Architecture layer purity (no Android imports in :domain) | 100% | Custom lint / ktlint rule |
| Time for reviewer to find and run a use case test | <2 minutes | Project layout review |
| Demo flow completion without crashes | 100% | Manual scripted demo |
| App size (debug APK) | <25 MB | APK Analyzer |

---

## Technical Considerations

- **Build System:** Gradle Kotlin DSL; version catalog (`libs.versions.toml`) for dependency management.
- **Kotlin / JDK:** Kotlin 1.9+, JDK 17, AGP 8+.
- **Concurrency:** Kotlin Coroutines + Flow; structured concurrency in ViewModels; `viewModelScope` for UI-bound work.
- **Compose:** BOM-based version management; `StateFlow.collectAsStateWithLifecycle()` to avoid leaks.
- **Navigation:** `androidx.navigation:navigation-compose` with type-safe routes (Kotlin 1.9+).
- **DI:** Hilt (or Koin — but Hilt is the more "Android-idiomatic" choice for a tech assessment).
- **State Machine:** Consider modeling room state transitions with a sealed class / `reduce` pattern in ViewModels to make logic explicit and testable.
- **Mocking Strategy:** A `FakeRoomStateDataSource` injected in `:app` debug variant; production variant would inject a real MQTT-backed source. Repository interfaces remain stable.
- **MQTT Simulation:** Use a simple in-process coroutine-based emitter (publish/subscribe channel) that mimics the semantics of an MQTT topic — so swapping in a real client (HiveMQ, Paho) later is a one-class change.
- **Testing:** JUnit5 + MockK + Turbine for Flow tests; Compose UI test with `createAndroidComposeRule`.
- **Build Flavors:** `mock` and `live` (or `debug` / `release` with a `useMockData` build config flag) to support staged rollout.
- **Performance:** Baseline render time tracked with Macrobenchmark for the dashboard; jank-free transitions as a quality bar.
- **Static Analysis:** ktlint, detekt, and (optionally) Spotless for formatting consistency.
