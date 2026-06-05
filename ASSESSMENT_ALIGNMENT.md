# Assessment Alignment

> Single source of truth: [`Product_Builder_Mobile_Android_Take_Home_Task.md`](Product_Builder_Mobile_Android_Take_Home_Task.md).
> This file maps every assessment requirement to the evidence in this repository and is honest about what is prototype-only.

---

## Assessment Summary

Take a loosely defined hospitality problem and turn it into a **validated, prototyped, ready-for-real-solution** Android application built in Kotlin, using mock data (no backend), and explain the product thinking around it: discovery, delivery strategy, adoption, working without a PM/designer, and AI-assisted development.

**Chosen problem:** *Smart Guest Room Management* — in-room friction for guests (confusing HVAC/lighting panels) combined with energy waste for hotels (HVAC/lighting spend in empty rooms). See [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md).

---

## Requirement-by-Requirement Checklist

| # | Assessment requirement | Status | Evidence |
|---|---|---|---|
| 1 | Identify a hospitality problem | ✅ Done | [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md), [Smart-Guest-Room-Management.md](Smart-Guest-Room-Management.md), [spec-01](specifications/spec-01-problem-statement.md) |
| 2 | Define who experiences it and why it matters | ✅ Done | [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md) (personas), [spec-01](specifications/spec-01-problem-statement.md) |
| 3 | Explain discovery and validation | ✅ Done | [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md), [spec-02](specifications/spec-02-discovery-validation.md) |
| 4 | Prototype the solution as a Kotlin Android app | 🟡 Scaffold + architecture in place; first feature slice pending | [ARCHITECTURE.md](ARCHITECTURE.md), [PROTOTYPE_OVERVIEW.md](PROTOTYPE_OVERVIEW.md), `app/`, `core/` |
| 5 | Use mock data, no backend required | ✅ Designed; `mock`/`live` build flavors exist | [app/build.gradle.kts](app/build.gradle.kts), [docs/architecture/04-mock-vs-live.md](docs/architecture/04-mock-vs-live.md) |
| 6 | Showcase the core user flow on device/emulator | 🟡 Core flow defined; placeholder app runs today | [PROTOTYPE_OVERVIEW.md](PROTOTYPE_OVERVIEW.md), [DEMO_SCRIPT.md](DEMO_SCRIPT.md) |
| 7 | Explain AI-assisted development | ✅ Done | [AI_DEVELOPMENT.md](AI_DEVELOPMENT.md), [spec-08](specifications/spec-08-ai-first-development.md) |
| 8 | Explain MVP and delivery strategy | ✅ Done | [DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md), [spec-05](specifications/spec-05-delivery-strategy.md) |
| 9 | Explain adoption strategy and metrics | ✅ Done | [ADOPTION_METRICS.md](ADOPTION_METRICS.md), [spec-06](specifications/spec-06-adoption-metrics.md) |
| 10 | Explain working without a PM or designer | ✅ Done | [NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md), [spec-07](specifications/spec-07-no-pm-designer.md) |
| 11 | Share code and prepare presentation/demo material | ✅ Repo + demo script | This repo, [DEMO_SCRIPT.md](DEMO_SCRIPT.md) |

Legend: ✅ complete · 🟡 in progress / partially built · ⬜ not started.

---

## Evidence in the App vs. Documentation

**In the app today (`com.mews.guestroom`):**
- Multi-module Gradle build: `:app`, `:core:common`, `:core:ui`.
- `mock` / `live` product flavors wired in [app/build.gradle.kts](app/build.gradle.kts) — the prototype/production seam is real at the build level.
- `:core:common` technical utilities (`DataResult`, dispatcher qualifiers); `:core:ui` for theme/tokens.
- A running placeholder screen ([AppRoot.kt](app/src/main/java/com/mews/guestroom/AppRoot.kt)) that compiles and launches.

**In documentation:**
- Full product thinking (problem, discovery, adoption, delivery) across the root docs and `specifications/`.
- Architecture decisions captured as ADRs in [docs/decisions/](docs/decisions/).
- The feature vertical-slice blueprint in [project-structure-blueprint.md](project-structure-blueprint.md).

---

## Known Gaps (honest)

- **No product feature module is implemented yet.** The first vertical slice (recommended: Room Controls / Dashboard) is scaffolded by convention but not coded. The app currently shows an intentional placeholder.
- **The live data sources do not exist** — only the mock side is planned for the prototype. This is by design (assessment requires no backend).
- **Tests** are described in the testing strategy but the demo-critical tests land with the first feature slice.

These gaps are scoped intentionally: the assessment values a validated problem, clear product strategy, and a credible Android foundation over a broad-but-shallow feature set. The roadmap is in [TODO.md](TODO.md).

---

## Suggested Talking Points for the Interview/Demo

1. **Problem first:** the dual guest-UX / hotel-ROI framing, and why hotels' existing IoT makes *now* the right time.
2. **Validation before code:** the zero-cost fake-door QR/NFC test and the go/no-go signals.
3. **Architecture as a product decision:** feature-isolated Clean Architecture, and why the `mock`/`live` flavor split is the production seam.
4. **Honest scope:** what is built (foundation + flavor seam) vs. what is intentionally deferred (feature slices).
5. **AI as a force multiplier:** where AI accelerated scaffolding/docs and where human judgement stayed in control.
6. **Adoption realism:** zero-friction check-in onboarding and the three-layer metric framework.
