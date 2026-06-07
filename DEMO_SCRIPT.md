# Demo Script

> A live, commented demo narrative for the take-home review (~5–7 minutes total, ~60–90s of app interaction).
> The Controls/Dashboard flow below is **implemented** and runs live on the `mock` flavor — demo it on an emulator/device.

---

## Opening Pitch (30s)

> "Hotel rooms frustrate guests *and* waste money for operators — confusing panels, lights and AC left running in empty rooms. Hotels already have the IoT hardware; it's just locked to staff. Smart Guest Room Management puts simple, energy-aware control in the guest's phone — solving the guest experience and the energy bill with one product."

---

## Problem Recap (30s)

- Guest pain: cryptic HVAC, key-card-in-slot power waste, can't adjust from bed.
- Hotel pain: HVAC/lighting is 40–60% of room energy; waste is invisible.
- Why now: existing Bluetooth/Zigbee/Matter infrastructure makes this low-CAPEX.
- One line: *"Same hardware, new value — for both sides."*

---

## Demo Setup (15s)

- Run the **`mockDebug`** flavor on an emulator/device — no backend, no setup.
- `./gradlew :app:installMockDebug` (JDK 17).
- Point out: the `mock`/`live` flavor split *is* the production seam.

---

## Step-by-Step Walkthrough (60–90s)

1. **Launch → Dashboard.** Room state at a glance: temperature, lights, blinds, energy mode.
2. **Adjust thermostat** with the slider/dial → state updates reactively; mock ambient temperature begins drifting toward the set point.
3. **Toggle lights / blinds** → immediate, believable state change with brief loading.
4. **Tap "Sleep Mode"** → one action dims lights, closes blinds, sets a sleep-friendly temperature (the orchestration use case).
5. **Show the error path** — the **Bathroom** light (intentionally faulty) renders a warning icon with its switch **disabled**, surfacing the unreachable device up-front. The underlying error path (a command against a faulty device returns an error) is still covered by data/ViewModel tests — the UI just makes the fault obvious rather than waiting for a failed tap.

*(`./gradlew :app:installMockDebug`, then open the app — the Dashboard is the start destination.)*

---

## What to Highlight Technically

- **Feature-isolated Clean Architecture** — each feature owns domain/data/presentation; no shared `:core:domain`/`:core:data` ([ARCHITECTURE.md](ARCHITECTURE.md)).
- **Reactive state** — `StateFlow<UiState>` + `collectAsStateWithLifecycle()`, one-shot `SharedFlow` events.
- **Mock realism** — in-memory `StateFlow` with simulated latency + drift behind the real repository contract.
- **The seam is real** — a data-source swap touches 0 lines outside the owning feature.

---

## What to Highlight From Product/Discovery

- Validation-before-code: the fake-door QR/NFC test and go/no-go signals ([PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md)).
- Dual-KPI discipline: every feature maps to guest experience or energy savings.
- Honest scope: one deep slice over many shallow ones.

---

## AI Contribution Talking Points

- AI scaffolded modules, mock data, and this docs suite; the engineer owned architecture and reconciled docs to real code ([AI_DEVELOPMENT.md](AI_DEVELOPMENT.md)).
- Headline: *AI amplified judgement, it didn't replace it.*

---

## Closing: MVP and Adoption (30s)

- **MVP:** make this one slice real for a pilot — live data source on the `live` flavor, reservation-window auth, telemetry vs. control rooms ([DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md)).
- **Adoption:** zero-friction check-in onboarding (captive portal / NFC card), measured across an adoption funnel, engagement, energy, and satisfaction ([ADOPTION_METRICS.md](ADOPTION_METRICS.md)).
- Close: *"Validated problem, credible prototype, clear path to production — without rewriting the app."*
