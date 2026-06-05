# Delivery Strategy

> MVP scope, speed vs. robustness trade-offs, and the path from prototype to production.
> Deep reference: [spec-05 Delivery Strategy](specifications/spec-05-delivery-strategy.md) and [docs/prototype-to-production.md](docs/prototype-to-production.md).

---

## Prototype Scope (what the take-home delivers)

- Multi-module, feature-isolated Android foundation that builds and runs.
- `mock` / `live` product flavors — the production seam, present from day one.
- One **vertical slice** (recommended: Room Controls / Dashboard) end-to-end: domain → mock data → presentation → `:app` wiring, with believable time-varying mock state.
- Demo-critical states (loading, content, error) and a scripted demo path.
- Zero backend, zero real hardware.

> Current status: foundation + flavor seam are in place; the first slice is the immediate next step (see [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md)).

---

## MVP Scope (first real-world release)

The MVP is the prototype's slice made **real** for one feature, in a limited hotel pilot:

- Live data source for the chosen feature (e.g. thermostat via room hub / MQTT).
- Reservation-window authorization (control auto-revokes at checkout).
- Zero-friction onboarding (captive portal / NFC card) for pilot rooms.
- Privacy-safe per-room telemetry to prove energy savings vs. control rooms.
- Crash reporting + basic analytics.

Everything else (access, services, notifications, profile) stays mocked or out of scope until the MVP proves value.

---

## What Changes From Prototype to MVP

| Layer | Prototype | MVP |
|---|---|---|
| `:feature:<name>:domain` | unchanged | **unchanged** |
| `:feature:<name>:presentation` | unchanged | unchanged (unless a genuinely new state appears) |
| `:feature:<name>:data` | `Mock<Feature>DataSource` | `Live<Feature>DataSource` + credentials, reconnect, auth |
| Flavor | `mock` | `live` |
| Telemetry / privacy | none | room-level metrics, consent, checkout purge |
| Failure handling | simulated | real offline/timeout/lock-failure paths |

The key claim: a data-source swap touches **0 lines outside the owning feature** (excluding `:app` DI/flavor wiring).

---

## Speed vs. Robustness Trade-offs

| We chose speed by… | We protected robustness by… |
|---|---|
| In-memory mock state, no persistence | Keeping the real repository contract in `domain` so the seam is honest |
| Material 3 defaults instead of bespoke design | Accessibility + dark mode coming free from M3 |
| One vertical slice, not all features | Building that slice through real module boundaries |
| AI-assisted scaffolding | Human review of every architectural decision ([AI_DEVELOPMENT.md](AI_DEVELOPMENT.md)) |
| Flavor-based mock/live toggle | Per-feature isolation so production work is incremental, not a rewrite |

---

## Production-Readiness Path

Feature-by-feature, per [docs/prototype-to-production.md](docs/prototype-to-production.md):

1. Keep domain (and usually presentation) unchanged.
2. Complete the live data source in `:feature:<name>:data`.
3. Bind the live data source to the `live` flavor.
4. Add real credential handling + reservation-window authorization.
5. Add privacy-safe telemetry and audit-log export.
6. Test lock-failure, timeout, offline, and staff-entry scenarios.
7. Security review for BLE / MQTT / gateway communication.

**Rule:** no production migration creates `:core:domain` or `:core:data`.

---

## Risks and Sequencing

| Risk | Sequencing decision |
|---|---|
| Building breadth before proving value | Ship one slice; gate breadth on pilot metrics |
| Hardware/integration uncertainty | Mock first; live data source isolated behind contract |
| Demo fragility | Mock state is deterministic + believable; demo-critical states tested |
| Scope creep without a PM | ICE/MoSCoW prioritisation ([NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md)) |

---

## Suggested Delivery Phases

1. **Phase 0 — Foundation** *(done)*: modules, flavors, theme, CI-able build.
2. **Phase 1 — Prototype slice**: Dashboard/Controls end-to-end on mock data + demo.
3. **Phase 2 — MVP pilot**: one live feature in ~10 pilot rooms, telemetry, A/B vs. control.
4. **Phase 3 — Expand**: add features by validated priority; introduce `api/impl` only where a stable public contract is needed.
5. **Phase 4 — Harden & scale**: offline, observability, multi-property rollout, security review.
