# Presentation Deck — Outline

> Slide-by-slide content for the take-home review deck (Miro / PDF). Target: **5–7 minutes**, ~60–90s of live app interaction. Drawn from [DEMO_SCRIPT.md](DEMO_SCRIPT.md), [README.md](README.md), and the docs suite. Speaker notes are in *italics*.

---

## Slide 1 — Title

**Smart Guest Room Management**
Android prototype · energy-aware in-room control · take-home assessment
David Ruiz

*One line: "Same hotel hardware, new value — for guest and operator."*

---

## Slide 2 — The Problem

- **Guests:** cryptic HVAC panels, key-card-in-slot power waste, can't adjust from bed.
- **Operators:** HVAC + lighting is **40–60%** of room energy; waste in empty rooms is invisible.
- **Why now:** existing Bluetooth / Zigbee / Matter infrastructure → low-CAPEX.

*Pain on both sides, solvable with one product. (PRODUCT_DISCOVERY.md)*

---

## Slide 3 — The Idea

Put **simple, energy-aware control in the guest's phone** — the hardware is already there, just locked to staff.

- Dual KPI: every feature maps to **guest experience** *or* **energy savings**.
- Validation-before-code: fake-door QR/NFC test, go/no-go signals.

---

## Slide 4 — What I Built (honest scope)

One feature, **end-to-end, on mock data** — the Room Dashboard:

- Thermostat (live drift toward setpoint), lights, blinds.
- One-tap **energy scenes** (Sleep / Away / Welcome) — multi-device orchestration.
- **Climate modes** (Auto Fan / Cooling) wired through the full command path.
- A deliberately **faulty device** to show the error path is handled.
- **Coming soon** placeholders for Services / Keys behind a persistent bottom nav.

*Other feature areas are documented, not coded — one deep slice over many shallow ones.*

---

## Slide 5 — Live Demo (60–90s)

Run `mockDebug` — no backend, no setup.

1. **Launch → Dashboard:** room state at a glance.
2. **Thermostat slider:** state updates reactively; ambient temp drifts toward the setpoint.
3. **Climate mode (Cooling):** mode switches; in Cooling the room never drifts warmer.
4. **Toggle lights / blinds:** immediate, believable change with brief loading.
5. **Sleep scene:** one tap dims lights, closes blinds, sets a sleep temperature.
6. **Faulty device:** the **Bathroom** light shows a warning icon and a disabled switch — the unreachable device is surfaced up-front, not hidden behind a happy path.

*Build: `./gradlew :app:installMockDebug` (JDK 17), open the app — Dashboard is the start destination.*

---

## Slide 6 — Architecture

**Feature-isolated Clean Architecture + MVVM**

- Each feature owns its `domain / data / presentation`; **no shared `:core:domain` / `:core:data`**.
- Reactive: `StateFlow<UiState>` + `collectAsStateWithLifecycle()`, one-shot `SharedFlow` events.
- Kotlin · Jetpack Compose + Material 3 · Hilt · Coroutines.

*Diagram: app → feature(domain/data/presentation) → core(ui/common). (ARCHITECTURE.md)*

---

## Slide 7 — The Production Seam Is Real

- `mock` / `live` **product flavors** from day one.
- `mock`: in-memory `StateFlow` with simulated latency + temperature drift.
- `live`: an **honest disconnected stub** — renders, every command returns "not connected".
- Swapping the data source touches **0 lines** outside the owning feature.

*This is how the prototype becomes production without a rewrite.*

---

## Slide 8 — Quality & Process

- **Strict TDD** for all logic: red → green → refactor, one PR per slice.
- Unit tests: use cases, mock/live data sources, ViewModel (Turbine).
- CI gate: build + tests + **ktlint/detekt** static analysis on every PR.
- UI / navigation are "build-alongside" + verified live on device.

---

## Slide 9 — Known Limitations (candid)

- Mock-only; `live` is a disconnected stub.
- No state persistence (resets on process death / rotation).
- Single hard-coded room; Services / Keys are placeholders.
- Compose smoke test deferred (Robolectric/emulator).

*Honesty builds trust — these are tracked, not hidden. (README → Known Limitations, ROADMAP)*

---

## Slide 10 — AI Contribution

- AI scaffolded modules, mock data, and the docs suite.
- The engineer owned **architecture decisions** and reconciled docs to real code.

*Headline: "AI amplified judgement — it didn't replace it." (AI_DEVELOPMENT.md)*

---

## Slide 11 — Path to Production

- **MVP pilot:** real `live` data source (room hub / MQTT), reservation-window auth, telemetry vs. control rooms.
- **Adoption:** zero-friction check-in onboarding (captive portal / NFC), measured across an adoption funnel — engagement, energy, satisfaction.

*(DELIVERY_STRATEGY.md, ADOPTION_METRICS.md)*

---

## Slide 12 — Close

**Validated problem · credible prototype · clear path to production — without rewriting the app.**

*Thank you — questions?*

---

## Appendix (backup slides, optional)

- Phased roadmap (Phase 0–4) — [ROADMAP.md](ROADMAP.md).
- Requirement-by-requirement mapping — [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md).
- Screenshots (light/dark) — README.
