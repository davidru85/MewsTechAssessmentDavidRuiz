# ROADMAP.md

> Time/phase view of the work. *What* changes per phase lives in [DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md); the granular backlog lives in [TODO.md](TODO.md). This file sequences them.

---

## Status Legend

✅ done · 🟡 in progress · ⬜ planned

---

## Phase 0 — Foundation ✅

Modular build that compiles and runs.

- ✅ Multi-module Gradle (`:app`, `:core:common`, `:core:ui`) + version catalog.
- ✅ `mock` / `live` product flavors (production seam present from day one).
- ✅ Material 3 theme scaffolding; Hilt wired.
- ✅ Architecture, ADRs, and full documentation suite.
- ✅ Placeholder app screen confirming build/launch/theme.

---

## Phase 1 — Prototype Slice ✅ (assessment-critical)

One feature end-to-end on mock data — the demo deliverable.

- ✅ `:feature:controls:domain` — models, `ControlsRepository`, use cases (energy-scene orchestration, temperature clamping).
- ✅ `:feature:controls:data` — `ControlsRepositoryImpl`, `MockControlsDataSource` (in-memory `StateFlow`, latency + temperature drift, faulty/unknown-device errors), Hilt module.
- ✅ `:feature:controls:presentation` — Dashboard screen (thermostat, light/blind toggles, energy scenes), `ViewModel`, `UiState`, nav entry.
- ✅ Replaced placeholder; wired Dashboard into the root `NavHost`.
- ✅ Demo-critical states: loading, content, and error (transient message). Empty intentionally dropped — a room always has controls.
- ✅ Tests: 33 JVM unit tests (use-case, MockDataSource, ViewModel via Turbine), all green. Compose smoke test deferred (needs emulator/Robolectric).
- ✅ Mock/live flavor seam made real (P1.2, PR #5) + Climate slider debounce (PR #6).
- ✅ Demo flow **verified on emulator** (Pixel_9a, `mockDebug`, 2026-06-05): content, temperature drift, the faulty Bathroom light surfaced via a warning icon + disabled switch, and Sleep-scene orchestration all render with no crashes/ANRs.
- ⬜ **Presenter steps (human):** time the live walkthrough to 60–90s and send the presentation one day before the session.

**Exit criteria:** scripted demo runs on the `mock` flavor without crashes ✅ (verified); [VALIDATION_CHECKLIST.md](VALIDATION_CHECKLIST.md) green except the presenter-only items (live rehearsal timing, presentation sent) and the deferred Compose smoke test.

---

## Phase 1.5 — Audit Action Items & Next Steps ⬜

Consolidated work packages identified during the project audit to polish the demo and prepare the codebase for Phase 2.

### Tier 1 — Pre-Demo Polish & Logistics (Highest Priority)
- ✅ Wire the Climate Mode Buttons: Add `setClimateMode` to repository/datasource, write use case, and enable button clicks.
- ✅ Placeholder Screen Routing: Add lightweight placeholder screens ("Coming soon") for bottom-nav Services and Keys tabs.
- ✅ Mark Faulty Devices: Add warning indicators to the Bathroom light row to make the deliberate error path obvious (warning icon + disabled switch, PR #12).
- ✅ README Gaps: Add a "Known Limitations" section in the main README.
- ✅ Deliver the Deck: Miro presentation deck prepared ([DECK_OUTLINE.md](DECK_OUTLINE.md) + Miro board with real screenshots); sending it one day prior is the remaining presenter step.

### Tier 2 — Accessibility & UI Testing
- ⬜ Compose Smoke Tests: Write a Robolectric-based Compose test to assert UI state rendering.
- ⬜ Accessibility Merger: Merge Lighting row TalkBack focus targets (`mergeDescendants = true`).
- ⬜ Polite Live Regions: Set `liveRegion = Polite` on progress/busy indicators.
- ✅ Reconcile Test Wording: Documentation states the unified test count (33 unit tests: domain 9, data 16, presentation 8).

### Tier 3 — Production Seam & Hardening
- ⬜ Offline UI State: Add dedicated disconnected state UI overlay for the `live` flavor.
- ⬜ Save Mock State: Persist mock control states to survive process deaths/rotations.
- ⬜ Detekt Scoping: Scope `MagicNumber` rules to layout directories instead of global suppression.
- ⬜ Inject Qualified Dispatchers: replace the hardcoded `Dispatchers.Default` in `provideControlsScope` with an injected `@DefaultDispatcher` from a `:core:common` `DispatchersModule` (binds `@IoDispatcher`/`@DefaultDispatcher`).

### Tier 4 — Feature Expansion & Telemetry
- ⬜ Access/Keyless Entry Slice: Implement `:feature:access` and wire to the "Keys" nav route.
- ⬜ In-App Event Log: Create an overlay showing domain events to connect telemetry with engineering.
- ⬜ Konsist Rules: Transition grep-based architecture checks to Konsist test-based rules.

**Exit criteria:** All Tier 1 and Tier 2 items complete, resolving immediate objections and accessibility gaps.

---

## Phase 2 — MVP Pilot ⬜

Make one feature real for ~10 pilot rooms.

- ⬜ `LiveControlsDataSource` (room hub / MQTT) bound to the `live` flavor.
- ⬜ Reservation-window authorization (auto-revoke at checkout).
- ⬜ Zero-friction onboarding (captive portal / NFC card).
- ⬜ Privacy-safe per-room telemetry; A/B vs. control rooms.
- ⬜ Crash reporting + basic analytics.

**Exit criteria:** measurable energy + satisfaction delta vs. control rooms ([ADOPTION_METRICS.md](ADOPTION_METRICS.md)).

---

## Phase 3 — Expand ⬜

Add features by validated priority (ICE/MoSCoW): Access, Services & chat, Notifications, Profile/Info. Introduce `api/impl` only where a stable public contract is genuinely needed.

---

## Phase 4 — Harden & Scale ⬜

Offline/degraded modes, observability, multi-property rollout, security review (BLE/MQTT/gateway), i18n + full accessibility pass. Details in [TODO.md](TODO.md) → Production Hardening.

---

## Immediate Next Action

Rehearse the [DEMO_SCRIPT.md](DEMO_SCRIPT.md) and prepare the presentation deck (Miro/PDF) to be sent one day prior to the session, then proceed to plan the Phase 2 MVP Pilot.
