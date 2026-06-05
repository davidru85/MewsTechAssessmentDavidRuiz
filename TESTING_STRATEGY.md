# TESTING_STRATEGY.md

> Assessment-appropriate testing — enough to prove the architecture and protect the demo, **not** enterprise coverage gates.
> Architectural context: [ARCHITECTURE.md](ARCHITECTURE.md); structural rules: [project-structure-blueprint.md](project-structure-blueprint.md) §8.

---

## Philosophy

Test what carries risk for *this* deliverable: the business logic, the ViewModel state machine, the demo path, and the architectural boundaries. Avoid testing framework code or trivial getters. A passing test that asserts nothing real is worse than no test.

---

## Test-Driven Development (mandatory)

**All new behaviour is written test-first, following red → green → refactor.** This is a hard guardrail, not a preference — production logic must not be authored before a failing test that specifies it.

### The TDD Protocol (mandatory)

Every phase has **exactly three sub-phases**, and **sub-phase 2 of every phase is a mandatory manual review by the project owner**. The agent must **STOP at each review gate** and must never self-approve, commit, push, or open a PR without that review.

| Phase | Sub-phase 1 — Code | Sub-phase 2 — Manual review ⛔ (project owner) | Sub-phase 3 — Integrate |
|---|---|---|---|
| 🔴 **Red** | Write the failing test; run it and confirm it fails for the **right reason** (missing code / unmet assertion — not a typo or config error) | Owner reviews the failing test | **Commit** the failing test |
| 🟢 **Green** | Write the **minimum** production code to pass; run the test and the module suite green | Owner reviews the implementation | **Commit and push** |
| 🔵 **Refactor** | Improve names/structure/duplication in production *and* test code under green; **no new behaviour**; re-run `ktlintCheck detekt test` | Owner reviews the refactor | **Commit, push, and open a pull request** |

**Hard rules:**

1. **Stop at every sub-phase 2.** The agent pauses and waits for the owner's explicit approval before doing sub-phase 3. No exceptions.
2. **No integration without review.** Nothing is committed (Red), pushed (Green), or turned into a PR (Refactor) until the owner has reviewed that phase.
3. **One behaviour per cycle**, then loop back to Red for the next.
4. If a review returns changes, redo sub-phase 1 of that phase and re-submit for review.

**Craft notes for sub-phase 1 (Code):** pick the next smallest behaviour (one rule/branch/state transition); arrange with fakes/`TestScope`; assert exactly one behaviour named `methodOrIntent_condition_expectedResult`; in Green write no speculative branches; in Refactor preserve behaviour (the green suite is the safety net).

Scope of the rule:

- **Applies to** use cases, repositories/data sources, ViewModels, and any logic with branching/state. These are unit-testable on the JVM and run in CI.
- **Pragmatic exceptions** (write alongside, not strictly test-first): Compose UI, navigation wiring, DI modules, and pure data/config — they are validated by build + demo + (future) instrumented tests rather than JVM unit tests. State *why* in the PR when an exception is taken.
- **TDD ordering shows in the commit history** where practical: a commit with the failing test, then a commit making it pass.

If you ever find yourself writing logic before its test, stop, revert or comment it out, and write the test first.

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
- **Compose smoke:** Dashboard renders; loading, content, and error (snackbar) states are reachable.

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

Before any demo, manually verify the scripted flow in [DEMO_SCRIPT.md](DEMO_SCRIPT.md) on the `mock` flavor, and confirm loading, content, and error states all appear. This is captured as a gate in [VALIDATION_CHECKLIST.md](VALIDATION_CHECKLIST.md).

---

## Explicitly Out of Scope (for the assessment)

- Hard coverage-percentage CI gates.
- End-to-end tests against real hardware/backends (none exist by design).
- Performance/load testing, security pen-testing — these are production-evolution items in [TODO.md](TODO.md).
