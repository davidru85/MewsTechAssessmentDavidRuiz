# Smart Guest Room Management

Android prototype for a smart hotel-room control app — built as a take-home assessment to demonstrate turning a loosely defined hospitality problem into a validated, prototyped, ready-for-real-solution Android application.

---

## Assessment Context

Source of truth: [`Product_Builder_Mobile_Android_Take_Home_Task.md`](Product_Builder_Mobile_Android_Take_Home_Task.md). The full requirement-by-requirement mapping is in [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md).

---

## Problem Summary

Hotel rooms frustrate guests (confusing HVAC/lighting, key-card-in-slot power waste) and waste money for operators (HVAC/lighting in empty rooms is 40–60% of room energy spend). Hotels already have the IoT hardware — it's just locked to staff. This app puts simple, energy-aware control in the guest's phone, solving the guest experience and the energy bill together. Details in [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md).

---

## Prototype Summary

- Multi-module, **feature-isolated Clean Architecture + MVVM** Android app in Kotlin.
- **Mock data only**, no backend — `mock` / `live` product flavors make the production seam real from day one.
- Core flow: a **Room Dashboard** with thermostat, lights/blinds, and one-tap Smart Energy Modes, driven by believable time-varying mock state.

**Current status (honest):** the build, modular foundation (`:app`, `:core:common`, `:core:ui`), and `mock`/`live` flavors exist and run; the app shows an intentional placeholder. The first product feature slice is the next step. See [PROTOTYPE_OVERVIEW.md](PROTOTYPE_OVERVIEW.md) and [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md).

---

## Tech Stack

| Concern | Choice |
|---|---|
| Language / JDK | Kotlin 2.0.21 / JDK 17 |
| Build | Gradle Kotlin DSL + version catalog, AGP 8.7.3 |
| UI | Jetpack Compose (BOM 2024.12.01) + Material 3 |
| State | Coroutines 1.9.0, `StateFlow` / `SharedFlow` |
| Navigation | Navigation Compose 2.8.4 |
| DI | Hilt 2.52 |
| Min / target SDK | 26 / 35 |
| Testing | JUnit, Coroutines Test, Turbine, Truth |

---

## How to Run

> Requires **JDK 17** (Gradle 8.9 fails on JDK 25; use `openjdk@17`).

```bash
# Build the mock flavor
./gradlew :app:assembleMockDebug

# Install on a running emulator/device
./gradlew :app:installMockDebug
```

Then launch the app from the device. Use the `mock` flavor for the demo — no backend or hardware required.

---

## How to Test

```bash
./gradlew test                      # unit tests
./gradlew :app:assembleMockDebug    # verify the app builds from a clean checkout
```

Testing strategy (domain use-case, ViewModel state via Turbine, Compose smoke tests) is described in [ARCHITECTURE.md](ARCHITECTURE.md) and [project-structure-blueprint.md](project-structure-blueprint.md) §8.

---

## Project Structure

```text
app/                     Composition root, Android app, mock/live flavors, root NavHost
core/common/             Result wrappers, dispatchers, clock, logging (technical only)
core/ui/                 Material 3 theme, design tokens, generic UI primitives
feature/<name>/          Per-feature domain / data / presentation (added per slice)
docs/                    Architecture notes, ADRs, design baselines, process
specifications/          Detailed product & technical specs (source material)
```

Feature modules own their own `domain`, `data`, and `presentation`; shared `core` modules are technical only (no shared `:core:domain` / `:core:data`). See [project-structure-blueprint.md](project-structure-blueprint.md).

---

## Documentation Map

| Document | Purpose |
|---|---|
| [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md) | Requirement-by-requirement mapping to evidence |
| [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md) | Problem, personas, validation, experiments |
| [PROTOTYPE_OVERVIEW.md](PROTOTYPE_OVERVIEW.md) | Core flow, screens, mock-data strategy |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Modular architecture and future evolution |
| [DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md) | MVP, speed vs. robustness, production path |
| [ADOPTION_METRICS.md](ADOPTION_METRICS.md) | Adoption plan and success metrics |
| [NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md) | Solo prioritisation and UX quality |
| [AI_DEVELOPMENT.md](AI_DEVELOPMENT.md) | How AI helped; where humans decided |
| [DEMO_SCRIPT.md](DEMO_SCRIPT.md) | Live commented demo narrative |
| [ROADMAP.md](ROADMAP.md) | Phased delivery timeline |
| [TODO.md](TODO.md) | Out-of-scope future work |
| [VALIDATION_CHECKLIST.md](VALIDATION_CHECKLIST.md) | Pre-submission / pre-demo gate |
| [TESTING_STRATEGY.md](TESTING_STRATEGY.md) | Test pyramid and per-slice coverage |
| [CI_CD.md](CI_CD.md) | CI strategy and future production CD path |
| [CONVENTIONS.md](CONVENTIONS.md) | Coding, naming, and process conventions |
| [GLOSSARY.md](GLOSSARY.md) | Domain, technical, and product vocabulary |
| [AGENTS.md](AGENTS.md) | Operating guide for AI/human contributors |
| [CONTEXT.md](CONTEXT.md) | Fast orientation snapshot |
| [MEMORY.md](MEMORY.md) | Durable project decision log |
| [project-structure-blueprint.md](project-structure-blueprint.md) | Structural source of truth |
| [docs/](docs/) | Architecture notes, ADRs, design & process |
| [specifications/](specifications/) | Detailed source specifications |

Process reference: [Branching & pull request strategy](docs/development/branching-and-pull-requests.md).

---

## License

Prepared as a take-home technical assessment. No production license is attached.
