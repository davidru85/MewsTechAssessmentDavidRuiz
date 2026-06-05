# TESTING_STRATEGY.md

> Assessment-appropriate testing — enough to prove the architecture and protect the demo, **not** enterprise coverage gates.
> Architectural context: [ARCHITECTURE.md](ARCHITECTURE.md); structural rules: [project-structure-blueprint.md](project-structure-blueprint.md) §8.

---

## Philosophy

Test what carries risk for *this* deliverable: the business logic, the ViewModel state machine, the demo path, and the architectural boundaries. Avoid testing framework code or trivial getters. A passing test that asserts nothing real is worse than no test.

---

## The Test Pyramid (here)

| Layer | What | Tools | Priority |
|---|---|---|---|
| **Domain / use case** | Business rules with fake repositories (pure JUnit, no Android) | JUnit, Truth, Coroutines Test | 🔴 Highest |
| **ViewModel / state** | `UiState` transitions, event emission | Turbine, Coroutines Test, Truth | 🔴 High |
| **Data / data source** | Mock data-source behaviour: latency, drift, event history | JUnit, Coroutines Test, Turbine | 🟡 Medium |
| **Compose UI smoke** | Primary demo path renders + key states show | Compose UI test | 🟡 Medium (demo-critical) |
| **App composition** | App wires the feature entry point | Compose/instrumented smoke | 🟢 Low |
| **Static checks** | No Android imports in `:feature:*:domain`; no cross-feature deps | ktlint/detekt + Gradle graph | 🔴 (cheap, enforces architecture) |

---

## What to Cover per Feature Slice

For the first slice (Controls / Dashboard):

- **Use cases:** `ToggleLightUseCase`, `SetTemperatureUseCase`, `ActivateSleepModeUseCase` — assert the resulting domain state and `DataResult` success/failure.
- **ViewModel:** initial state → loading → content; error path; one-shot events. Verify with Turbine on the `StateFlow`.
- **Mock data source:** emits believable time-varying state; command applies latency then updates state.
- **Compose smoke:** Dashboard renders; loading, success, empty, and error states are reachable.

---

## Conventions

- Inject dispatchers (`:core:common`) and use `StandardTestDispatcher` / `runTest` — never real `Dispatchers`.
- Use fakes for repositories in domain tests; do not mock what you own unnecessarily.
- One behaviour per test; name as `methodOrIntent_condition_expectedResult`.
- Keep tests in the owning module's `src/test` (unit) or `src/androidTest` (Compose/instrumented).
- Shared test helpers go in `:core:testing` (technical only) when introduced.

---

## Running Tests

```bash
./gradlew test                       # all unit tests
./gradlew :feature:controls:test     # one feature's unit tests (once it exists)
./gradlew connectedMockDebugAndroidTest   # Compose/instrumented (device/emulator)
./gradlew :app:assembleMockDebug     # build-as-smoke-test from clean checkout
```

---

## Demo-Critical Path Verification

Before any demo, manually verify the scripted flow in [DEMO_SCRIPT.md](DEMO_SCRIPT.md) on the `mock` flavor, and confirm loading/success/empty/error states all appear. This is captured as a gate in [VALIDATION_CHECKLIST.md](VALIDATION_CHECKLIST.md).

---

## Explicitly Out of Scope (for the assessment)

- Hard coverage-percentage CI gates.
- End-to-end tests against real hardware/backends (none exist by design).
- Performance/load testing, security pen-testing — these are production-evolution items in [TODO.md](TODO.md).
