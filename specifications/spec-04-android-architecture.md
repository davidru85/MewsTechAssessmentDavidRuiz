# Detailed Specification for The Android Prototype: Architecture and Craft

## Section Overview (Executive Summary)

This section defines the technical foundation of the prototype: a Kotlin-based
Android application built with feature-isolated Clean Architecture, MVVM,
reactive state management via Kotlin Coroutines and Flow, and Jetpack Compose.

The required architecture is not a shared `core:data` / `core:domain` model.
Each business feature owns its own `domain`, `data`, and `presentation` modules.
The shared `core` area is limited to cross-cutting technical utilities that do
not contain product-specific business rules.

The goal is to ship a credible, demoable prototype that proves engineering
craft, strict modular boundaries, and a clean path from prototype mode to
production mode without coupling unrelated features together.

---

## Core Components & Features

- **Feature-Isolated Module Structure:** Multi-module Gradle setup where each
  feature is split into its own layers, for example
  `:feature:access:domain`, `:feature:access:data`, and
  `:feature:access:presentation`.
- **Domain Per Feature:** Pure-Kotlin models, repository contracts, and use
  cases live inside the owning feature domain module. For example,
  `UnlockDoorUseCase` and `AccessRepository` would belong to
  `:feature:access:domain` if the Access feature is selected, not to a shared
  domain module.
- **Data Per Feature:** Repository implementations, DTOs, mappers, mock data
  sources, and production data sources live inside the owning feature data
  module. For example, access mock/live door integrations belong to
  `:feature:access:data`.
- **Presentation Per Feature:** Compose screens, ViewModels, UI state, UI
  events, and feature navigation live inside the owning feature presentation
  module.
- **Shared Core Is Technical Only:** `:core:common`, `:core:ui`, and
  `:core:testing` may exist, but they must not contain feature business models,
  repository contracts, use cases, or data-source implementations.
- **Composition Root:** `:app` owns the Android application, build flavors,
  app-wide navigation host, and the final dependency graph. It depends on
  feature presentation modules and the data modules needed to satisfy DI.
- **Reactive Prototype Data:** Mock feature data sources use `StateFlow` /
  `MutableStateFlow` to simulate real device state without backend hardware.
- **Mock vs. Live Swap:** The mock/live boundary lives inside each feature's
  data module, selected by build flavor or DI binding. UI and feature domain
  code remain unchanged.
- **Use Case Pattern:** Each user-facing action wraps a small feature-owned use
  case, keeping ViewModels thin and testable.
- **Demo Flow:** A scripted 60-90 second demo path should be defined after the
  first feature is selected and implemented.

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Hiring Manager / Tech Reviewer
- **Pain:** Wants to see architectural thinking, not just screens.
- **Pain:** Needs proof that feature boundaries are real and enforceable.
- **Pain:** Looks for idiomatic Kotlin, dependency direction discipline,
  concurrency awareness, and testable business logic.

### Engineering Team
- **Pain:** Will need to add or remove features without accidentally touching
  unrelated domains.
- **Pain:** Needs a project layout where ownership is obvious from the path.

### Product / Demo Audience
- **Pain:** Wants a prototype that feels alive and believable, even while data
  is simulated.

### Future Maintenance Developer
- **Pain:** Needs to understand which module owns each model, use case, and
  data source without hunting through a large shared domain package.

---

## Functional Requirements (FRs) / User Stories

1. **As a tech reviewer**, I want each feature to own its data, domain, and
   presentation layers, **so that** I can verify strict feature isolation.
2. **As a tech reviewer**, I want feature domain modules to be pure Kotlin with
   no Android imports, **so that** business logic is portable and unit-testable.
3. **As a future maintainer**, I want unrelated features to have no compile-time
   dependency on each other, **so that** changes remain local.
4. **As a tech reviewer**, I want the first implemented feature to demonstrate a
   complete vertical slice, **so that** the architecture is proven by working
   code rather than folder names.
5. **As a demo viewer**, I want the app to show live-feeling state and react to
   commands immediately, **so that** the prototype feels real.
6. **As an engineering lead**, I want mock/live implementation choices hidden
   behind feature-owned repository contracts, **so that** production migration
   does not rewrite UI or use cases.
7. **As a tech reviewer**, I want ViewModels to expose `StateFlow<UiState>` and
   one-shot `SharedFlow<Event>` where needed, **so that** state handling is
   explicit and lifecycle-safe.
8. **As a solo engineer**, I want shared UI tokens and test utilities to be
   reusable, **so that** features stay visually coherent without sharing
   business logic.
9. **As a reviewer**, I want tests at the feature-domain and feature-presentation
   boundaries, **so that** isolation and behavior are both visible.
10. **As a reviewer**, I want the project to build from a clean checkout, **so
    that** architecture claims can be validated quickly.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| Build success on clean clone | 100% | `./gradlew assembleMockDebug` |
| Feature-domain Android imports | 0 | static analysis / grep |
| Cross-feature dependencies | 0 | Gradle dependency graph inspection |
| First selected feature vertical slice completion | 100% | manual demo + unit tests |
| Code coverage on implemented feature domain | >80% | unit test report |
| UI freeze / ANR during demo | 0 | manual demo / Logcat |
| Demo flow completion without crashes | 100% | scripted demo |
| Time for reviewer to find feature use cases/tests | <2 minutes | project layout review |

---

## Required Module Shape

The canonical feature shape is:

```text
:feature:<name>:domain
  Pure Kotlin. Models, repository interfaces, use cases.

:feature:<name>:data
  Repository implementations, DTOs, mappers, mock/live data sources.
  Depends on :feature:<name>:domain.

:feature:<name>:presentation
  Compose screens, ViewModels, UI state, UI events, navigation.
  Depends on :feature:<name>:domain and technical core UI/common modules.
```

Allowed shared modules:

```text
:core:common
  Cross-cutting Kotlin utilities: results, dispatchers, clock, logging.

:core:ui
  Theme, design tokens, and generic UI primitives with no feature business
  knowledge.

:core:testing
  Shared test rules and helpers only.
```

Forbidden modules for this project:

```text
:core:domain
:core:data
```

Those modules would centralize business ownership and weaken the required
feature isolation.

Optional `api/impl` evolution:

```text
:feature:<name>:api
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
:feature:<name>:impl
```

The `api/impl` pattern should be applied if necessary, specifically when a
feature needs a stable public contract while keeping implementation details
private. It is not required for the initial scaffold or for every feature by
default. It must preserve feature isolation and must not introduce shared
`:core:domain` or `:core:data` modules.

---

## Technical Considerations

- **Build System:** Gradle Kotlin DSL, version catalog, and convention plugins
  for repeated Android/Kotlin configuration.
- **Kotlin / JDK:** Kotlin 1.9+, JDK 17, AGP 8+.
- **Concurrency:** Kotlin Coroutines + Flow; structured concurrency in
  ViewModels; injected dispatchers for testability.
- **Compose:** Material 3, `StateFlow.collectAsStateWithLifecycle()`, dark mode,
  and dynamic color where supported.
- **Navigation:** `androidx.navigation:navigation-compose`; app-level root
  graph composes feature navigation entries without features depending on each
  other.
- **DI:** Hilt. Feature data modules bind feature repository interfaces to mock
  or live implementations. Presentation receives use cases through Hilt.
- **Mocking Strategy:** Mock implementations live next to the feature data they
  replace, not in a global shared data module.
- **Testing:** Unit tests for feature domain use cases, ViewModel state tests
  with Turbine, and a small app smoke test for the primary demo path.
- **Static Analysis:** ktlint / detekt rules should reject Android imports in
  `:feature:*:domain` and reject feature-to-feature dependencies.
