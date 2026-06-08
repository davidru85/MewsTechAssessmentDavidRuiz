# Smart Guest Room Management - Android Project Structure Blueprint

> Foundational project layout for the Smart Guest Room Management Android
> prototype using Kotlin, Jetpack Compose, MVVM, and feature-isolated Clean
> Architecture.

This document is the structural source of truth for the Android codebase.
Whenever it conflicts with older notes, this blueprint wins.

---

## 1. Architecture Rule

The project requires **feature-isolated architecture**.

Each product feature owns its own layers:

```text
:feature:<feature>:domain
:feature:<feature>:data
:feature:<feature>:presentation
```

The app must not use shared `:core:domain` or `:core:data` modules. Domain
models, repository contracts, use cases, repository implementations, DTOs,
mappers, and data sources belong to the feature that owns the behavior.

Shared `core` modules are allowed only for technical, cross-cutting concerns:

```text
:core:common   result wrappers, dispatcher qualifiers
:core:ui       generic theme/tokens/components without business knowledge
:core:testing  shared test utilities only
```

---

## 2. Architectural Foundation

| Decision | Choice |
|---|---|
| Language | Kotlin, JDK 17 |
| Build system | Gradle Kotlin DSL + Version Catalog |
| UI toolkit | Jetpack Compose + Material 3 |
| Architecture | Feature-isolated Clean Architecture + MVVM |
| State model | Coroutines + `StateFlow` / `SharedFlow` |
| Navigation | Compose Navigation with feature entry points |
| DI | Hilt |
| Prototype data | In-memory feature-owned mock data sources |
| Production path | Feature-owned live data sources behind the same domain contract |
| Testing | JUnit, Coroutines Test, Turbine, focused Compose tests |

---

## 3. Product Scope To Feature Modules

| Product area | Modules |
|---|---|
| Security & Access Control | `:feature:access:domain`, `:feature:access:data`, `:feature:access:presentation` |
| Room Controls & Automation | `:feature:controls:domain`, `:feature:controls:data`, `:feature:controls:presentation` |
| Dashboard / Room Overview | `:feature:dashboard:domain`, `:feature:dashboard:data`, `:feature:dashboard:presentation` |
| Chat & Hotel Services | `:feature:services:domain`, `:feature:services:data`, `:feature:services:presentation` |
| Notifications & Alerts | `:feature:notifications:domain`, `:feature:notifications:data`, `:feature:notifications:presentation` |
| General Info & Profile | `:feature:info:*`, `:feature:profile:*` |

These are module-shape examples, not an implementation order. The first feature
to implement should be chosen from product priority, not from this table.

---

## 4. Dependency Graph

Arrows point from dependent to dependency.

```text
                         :app
                          |
       +------------------+------------------+
       |                                     |
:feature:<name>:presentation       future feature presentation
       |
       v
:feature:<name>:domain <---- :feature:<name>:data
       ^                              |
       |                              v
       +--------- :core:common <------+

:feature:<name>:presentation -> :core:ui
:feature:<name>:presentation -> :core:common
:feature:<name>:data         -> :core:common
:feature:<name>:domain       -> :core:common
:app                         -> feature presentation + feature data
```

Rules:

- A feature `presentation` module may depend on its own `domain`.
- A feature `data` module may depend on its own `domain`.
- A feature `domain` module may depend only on technical common modules and
  pure Kotlin libraries.
- No feature module may depend on another feature module, except `:app` which
  composes the application.
- `presentation` must not depend on `data`.
- `domain` must not depend on Android, Compose, Hilt, or data implementations.
- `core` must not contain hospitality business rules.

---

## 5. Top-Level Layout

```text
SmartGuestRoom/
├── app/
├── core/
│   ├── common/
│   ├── ui/
│   └── testing/
├── feature/
│   └── <name>/
│       ├── domain/
│       ├── data/
│       └── presentation/
├── build-logic/
├── gradle/
├── docs/
├── specifications/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── README.md
└── LICENSE
```

---

## 6. Feature Vertical Slice Template

When a feature is selected, implement it as a full vertical slice.

```text
feature/<name>/domain/
├── model/
│   └── <Feature>Model.kt
├── repository/
│   └── <Feature>Repository.kt
└── usecase/
    ├── Observe<Feature>StateUseCase.kt
    └── Execute<Feature>ActionUseCase.kt

feature/<name>/data/
├── di/
│   └── <Feature>DataModule.kt
├── repository/
│   └── <Feature>RepositoryImpl.kt
└── source/
    ├── <Feature>DataSource.kt
    ├── Mock<Feature>DataSource.kt
    └── Live<Feature>DataSource.kt

feature/<name>/presentation/
├── <Feature>Screen.kt
├── <Feature>UiState.kt
├── <Feature>ViewModel.kt
└── navigation/
    └── <Feature>Navigation.kt
```

Expected flow:

```text
<Feature>Screen
  -> <Feature>ViewModel
  -> feature-owned use case
  -> <Feature>Repository
  -> <Feature>RepositoryImpl
  -> Mock<Feature>DataSource / Live<Feature>DataSource
```

---

## 7. Mock vs. Live Rule

Mock/live differences stay inside the owning feature data module.

For any feature:

| Concern | Mock | Live |
|---|---|---|
| State source | `Mock<Feature>DataSource` | `Live<Feature>DataSource` |
| Command handling | in-memory delay + state update | real integration |
| Event history | in-memory list | backend / gateway service |
| Domain impact | none | none |
| Presentation impact | none | none |

---

## 8. Testing Strategy

- Feature domain tests validate use cases with fake repositories.
- Feature data tests validate repository/data-source behavior.
- Feature presentation tests validate ViewModel state transitions with Turbine.
- App smoke tests validate that the app composes the feature entry point.
- Static checks reject Android imports in `:feature:*:domain`.

---

## 9. Optional api/impl Evolution

The initial architecture does not require an `api/impl` split for every feature.
The default feature shape remains:

```text
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
```

The `api/impl` pattern should be applied when it becomes necessary to expose a
stable public feature contract while hiding implementation details. Typical
triggers:

- another feature needs to depend on a narrow public contract;
- `:app` should consume a feature entry point without seeing internal screens,
  ViewModels, repositories, or data sources;
- the feature grows large enough that compile-time isolation matters;
- separate teams own different features;
- the feature needs a stable plugin-like integration surface.

When needed, the preferred shape is:

```text
:feature:<name>:api
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
:feature:<name>:impl
```

Where `api` contains only the public contract and `impl` aggregates the concrete
feature wiring. The split must not reintroduce shared `:core:domain` or
`:core:data`.

---

## 10. ADRs To Capture

The project should keep ADRs for:

- Feature-isolated architecture over shared core domain/data.
- Applying `api/impl` only when a stable public feature contract is needed.
- Hilt as DI.
- StateFlow / SharedFlow state model.
- Mock/live data-source boundary per feature.
- Compose navigation feature entry points.
- Material 3 / dark mode parity.
