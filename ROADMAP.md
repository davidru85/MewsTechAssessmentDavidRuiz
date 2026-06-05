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

## Phase 1 — Prototype Slice 🟡 (assessment-critical)

One feature end-to-end on mock data — the demo deliverable.

- ⬜ `:feature:controls:domain` — models, `ControlsRepository`, use cases (incl. energy-mode orchestration).
- ⬜ `:feature:controls:data` — `ControlsRepositoryImpl`, `MockControlsDataSource` (in-memory `StateFlow`, latency + temperature drift), Hilt module.
- ⬜ `:feature:controls:presentation` — Dashboard screen, thermostat, light/blind toggles, `ViewModel`, `UiState`, nav entry.
- ⬜ Replace placeholder; wire Dashboard into the root `NavHost`.
- ⬜ Demo-critical states: loading / success / empty / error.
- ⬜ Tests: use-case (JUnit), ViewModel (Turbine), Compose smoke test.
- ⬜ Rehearse [DEMO_SCRIPT.md](DEMO_SCRIPT.md).

**Exit criteria:** scripted 60–90s demo runs on the `mock` flavor without crashes; [VALIDATION_CHECKLIST.md](VALIDATION_CHECKLIST.md) green.

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
