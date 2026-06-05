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
- ✅ Tests: 18 JVM unit tests (use-case, MockDataSource, ViewModel via Turbine), all green. Compose smoke test deferred (needs emulator/Robolectric).
- ✅ Mock/live flavor seam made real (P1.2, PR #5) + Climate slider debounce (PR #6).
- ✅ Demo flow **verified on emulator** (Pixel_9a, `mockDebug`, 2026-06-05): content, temperature drift, error snackbar (faulty Bathroom light), and Sleep-scene orchestration all render with no crashes/ANRs.
- ⬜ **Presenter steps (human):** time the live walkthrough to 60–90s and send the presentation one day before the session.

**Exit criteria:** scripted demo runs on the `mock` flavor without crashes ✅ (verified); [VALIDATION_CHECKLIST.md](VALIDATION_CHECKLIST.md) green except the presenter-only items (live rehearsal timing, presentation sent) and the deferred Compose smoke test.

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

Implement the **Phase 1 Controls/Dashboard slice** — the only item standing between the current build and a complete assessment prototype.
