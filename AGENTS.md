# AGENTS.md

> Operating guide for AI coding agents (and humans) working in this repository.
> Read this first, then [CONTEXT.md](CONTEXT.md) for orientation. This file is the standing instruction set for a Vibecoding workflow.

---

## Project in One Line

Android prototype — *Smart Guest Room Management* — built as a take-home assessment. Kotlin, Jetpack Compose, mock data, no backend. See [README.md](README.md) and [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md).

---

## Golden Rules (do not violate)

1. **Feature isolation.** Each product feature owns its own `:feature:<name>:domain`, `:data`, `:presentation`. **Never** create `:core:domain` or `:core:data`. `core` is technical only.
2. **No cross-feature dependencies.** Only `:app` composes features. A feature module never depends on another feature module.
3. **Pure domain.** `:feature:*:domain` is pure Kotlin — no Android, Compose, Hilt, or data-layer imports.
4. **`presentation` must not depend on `data`.** It depends on `domain` + `:core:ui`/`:core:common`.
5. **Mock/live seam lives in the feature `data` module**, selected by the `mock`/`live` product flavor. Swapping a data source must touch **0 lines** outside the owning feature (excluding `:app` DI/flavor wiring).
6. **Honesty over aspiration.** Documentation must match real repo state. If a feature isn't implemented, say so. Do not claim finished screens that don't exist.
7. **`api/impl` is optional and future-only.** Never introduce it by default ([ADR-0006](docs/decisions/0006-api-impl-when-needed.md)).
8. **TDD is compulsory, and follows the mandatory protocol.** All new behaviour is written **test-first** (red → green → refactor). Each phase has three sub-phases — code, **manual review by the project owner**, then integrate (Red→commit, Green→commit+push, Refactor→commit+push+PR). **STOP at every review gate; never commit, push, or open a PR without the owner's review.** Full protocol in [TESTING_STRATEGY.md](TESTING_STRATEGY.md#the-tdd-protocol-mandatory).

---

## Build & Run

> **JDK 17 required.** Gradle 8.9 fails on JDK 25 — use `openjdk@17`.

```bash
./gradlew :app:assembleMockDebug     # build mock flavor
./gradlew :app:installMockDebug      # install on emulator/device
./gradlew test                       # unit tests
```

Use the **`mock`** flavor for all demos and local work. No backend or hardware is required.

---

## Where Things Live

| Need | Location |
|---|---|
| Composition root, root NavHost, flavors | `:app` (`com.mews.guestroom`) |
| Result wrappers, dispatchers, clock | `:core:common` |
| Theme, design tokens, generic UI | `:core:ui` |
| A product feature | `:feature:<name>:{domain,data,presentation}` |
| Architecture rules | [ARCHITECTURE.md](ARCHITECTURE.md), [project-structure-blueprint.md](project-structure-blueprint.md) |
| Decisions | [docs/decisions/](docs/decisions/) (ADRs) |
| Conventions | [CONVENTIONS.md](CONVENTIONS.md) |
| Testing | [TESTING_STRATEGY.md](TESTING_STRATEGY.md) |

---

## How to Add a Feature (vertical slice)

Follow the template in [project-structure-blueprint.md](project-structure-blueprint.md) §6:

Work **test-first** at every layer (red → green → refactor — see Golden Rule 8):

1. `:feature:<name>:domain` — write use-case/model tests first, then models, `<Feature>Repository` contract, and use cases to green.
2. `:feature:<name>:data` — write data-source tests first (commands, latency, error, drift), then `Mock<Feature>DataSource`, `<Feature>RepositoryImpl`, and the Hilt module to green.
3. `:feature:<name>:presentation` — write the `<Feature>ViewModel` state/event tests (Turbine) first, then the ViewModel/`UiState` to green. Build the `<Feature>Screen` + navigation entry point alongside (UI is the pragmatic non-unit-tested exception).
4. Register the modules in [settings.gradle.kts](settings.gradle.kts) and wire into `:app`.
5. Update status in [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md) and [ROADMAP.md](ROADMAP.md).

---

## Definition of Done

- **Behaviour was developed test-first** following the mandatory [TDD Protocol](TESTING_STRATEGY.md#the-tdd-protocol-mandatory) — red → green → refactor, each phase passing the owner's manual-review gate before commit / push / PR. **Non-negotiable.**
- Slice builds: `./gradlew :app:assembleMockDebug` (JDK 17).
- Tests pass for the touched layers.
- No cross-feature deps; `:feature:*:domain` has no Android imports.
- Demo-critical states modelled (loading, content, and error events).
- Docs reconciled with reality; relevant ADR added/updated.
- Merged via a per-feature PR ([docs/development/branching-and-pull-requests.md](docs/development/branching-and-pull-requests.md)).

---

## Guardrails

- Never feed real guest data or PMS credentials to AI tools — synthetic data only.
- Keep changes scoped to the owning feature.
- Prefer Material 3 defaults over bespoke components ([NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md)).
- When uncertain about scope, check [ROADMAP.md](ROADMAP.md) and [TODO.md](TODO.md) before expanding surface area.
