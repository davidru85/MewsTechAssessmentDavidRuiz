# Architecture

> Lightweight, feature-isolated modular architecture for an Android prototype that can evolve toward production.
> Deep references: [project-structure-blueprint.md](project-structure-blueprint.md), [spec-04 Android Architecture](specifications/spec-04-android-architecture.md), [docs/architecture/](docs/architecture/), and the ADRs in [docs/decisions/](docs/decisions/).

---

## Architecture Summary

The app uses **feature-isolated Clean Architecture + MVVM**. Each *product* feature owns its own `domain`, `data`, and `presentation` modules. Shared `core` modules are **technical only** — there is deliberately **no shared `:core:domain` or `:core:data`**, so business ownership is always obvious from the module path.

This is a lightweight modular architecture, chosen for prototype speed while keeping production seams real and local.

---

## Module / Layer Overview

Implemented today:

```text
:app            Composition root, Android application, build flavors, root NavHost
:core:common    Result wrappers (DataResult), dispatcher qualifiers, clock, logging
:core:ui        Material 3 theme, design tokens, generic UI primitives (no business logic)
```

Canonical feature shape (added per feature when implemented):

```text
:feature:<name>:domain        Pure Kotlin: models, repository contracts, use cases
:feature:<name>:data          Repository impls, mappers, Mock/Live data sources, DI
:feature:<name>:presentation  Compose screens, ViewModels, UiState, events, navigation
```

Dependency rules: `presentation → domain`, `data → domain`, all features `→ :core:common`, presentation `→ :core:ui`. No feature depends on another feature; only `:app` composes them. `domain` has **no** Android/Compose/Hilt imports.

---

## State Management

- Screen state: `StateFlow<UiState>` collected with `collectAsStateWithLifecycle()`.
- One-shot events: `SharedFlow<Event>` with `replay = 0`.
- ViewModels expose immutable flows + public intent functions; use cases return domain values or `DataResult<T>`.
- See [docs/architecture/02-state-management.md](docs/architecture/02-state-management.md) and [ADR-0003](docs/decisions/0003-stateflow-vs-sharedflow.md).

---

## Navigation

`:app` owns the root `NavHost`. Each feature presentation module exposes a small entry point (e.g. `NavGraphBuilder.featureGraph()`). Features never navigate directly into each other — cross-feature journeys are coordinated by `:app`. See [docs/architecture/03-navigation.md](docs/architecture/03-navigation.md) and [ADR-0005](docs/decisions/0005-compose-navigation-feature-entry-points.md).

---

## Mock Data and Repository Strategy

The repository **contract** lives in the feature's `domain`; the mock and live **implementations** live in the feature's `data` module. Selection is by product flavor (`mock` / `live`, already defined in [app/build.gradle.kts](app/build.gradle.kts)) or Hilt binding.

| | mock | live |
|---|---|---|
| Data source | `Mock<Feature>DataSource` (in-memory `StateFlow`, simulated latency + drift) | `Live<Feature>DataSource` (MQTT/BLE/PMS) |
| Repository / contract | `<Feature>RepositoryImpl` / `<Feature>Repository` | same |
| Domain & presentation | unchanged | unchanged |

See [docs/architecture/04-mock-vs-live.md](docs/architecture/04-mock-vs-live.md) and [ADR-0004](docs/decisions/0004-mock-live-boundary-per-feature.md).

---

## Error / Loading / Content States

Every screen's `UiState` models loading and content explicitly, with errors surfaced as one-shot events (e.g. a snackbar). `DataResult<T>` in `:core:common` carries success/failure from data → domain → ViewModel so failures are first-class, not exceptions that crash the demo.

---

## Testing Approach

Assessment-appropriate, not enterprise coverage gates:

- **Domain/use-case tests** with fake repositories (pure JUnit).
- **ViewModel state tests** with Turbine over the `StateFlow`.
- **Compose smoke test** for the primary demo path.
- **Static checks** rejecting Android imports in `:feature:*:domain` and cross-feature dependencies.

See [project-structure-blueprint.md](project-structure-blueprint.md) §8.

---

## Future Production Evolution

Production hardening happens **feature by feature** (see [docs/prototype-to-production.md](docs/prototype-to-production.md)): complete the live data source, bind it to the `live` flavor, add credential handling + reservation-window authorization, privacy-safe telemetry, offline/error handling, and a security review for BLE/MQTT/gateway communication. Domain and presentation stay stable throughout.

---

## Optional `api/impl` Evolution

The prototype uses lightweight modularisation. It is compatible with future `api/impl` splitting, but that split is not imposed now because the assessment values a validated Android prototype and clear product strategy over enterprise-level module isolation. If the product moved toward production, `api/impl` would be introduced selectively for stable public contracts or modules consumed by multiple teams/features.

Trigger conditions and the preferred `:feature:<name>:api` / `:impl` shape are captured in [project-structure-blueprint.md](project-structure-blueprint.md) §9 and [ADR-0006](docs/decisions/0006-api-impl-when-needed.md). Any such split must preserve feature isolation and must **not** reintroduce shared `:core:domain` or `:core:data`.

---

## Tech Stack

| Concern | Choice |
|---|---|
| Language / JDK | Kotlin 2.0.21 / JDK 17 |
| Build | Gradle Kotlin DSL + version catalog, AGP 8.7.3 |
| UI | Jetpack Compose (BOM 2024.12.01) + Material 3 |
| State | Coroutines 1.9.0, `StateFlow` / `SharedFlow` |
| Navigation | Navigation Compose 2.8.4 |
| DI | Hilt 2.52 ([ADR-0002](docs/decisions/0002-hilt-over-koin.md)) |
| Min / target SDK | 26 / 35 |
| Testing | JUnit, Coroutines Test, Turbine, Truth |
