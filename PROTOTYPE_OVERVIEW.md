# Prototype Overview

> What the Android prototype demonstrates, how mock data simulates real behaviour, and what is intentionally out of scope.
> Honest current state: the **Controls/Dashboard** slice is built end-to-end on mock data and runs on the `mock` flavor; other feature areas are documented, not coded. See [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md) for status.

---

## Prototype Goal

Prove **mobile product thinking and Android craft** for Smart Guest Room Management without any backend — using mock data to make a single room "feel alive." The prototype is a credible foundation a reviewer can build, run, and reason about, not a feature-complete product.

---

## Core User Flow

The demo-critical flow is the **Room Dashboard**:

1. Guest opens the app → lands on a **Dashboard** showing room state at a glance (temperature, lights, blinds, energy mode).
2. Guest adjusts the **thermostat** (slider/dial) → state updates reactively.
3. Guest toggles **lights / blinds**.
4. Guest taps a **Smart Energy Mode** ("Sleep Mode") → multiple subsystems change in one action.
5. Mock data **simulates drift** (e.g. ambient temperature gradually moves toward the set point while AC runs), so the screen is not static.

This is the 60–90 second path scripted in [DEMO_SCRIPT.md](DEMO_SCRIPT.md).

---

> UI design is specified in [DESIGN.md](DESIGN.md) — the canonical source of truth for theme, layout, components, and the design iteration backlog.

## Screen Inventory

| Screen | Purpose | State today |
|---|---|---|
| **Dashboard** ([DashboardScreen.kt](feature/controls/presentation/src/main/kotlin/com/mews/guestroom/feature/controls/presentation/DashboardScreen.kt)) | Room state at a glance: climate, scenes, lights, blinds | ✅ Implemented |
| **Climate control** | Thermostat set point via slider (clamped 16–28°C) | ✅ Implemented (in Dashboard) |
| **Lighting & blinds** | Per-device toggles + blind position | ✅ Implemented (in Dashboard) |
| Access / keyless entry | Phone-as-key + audit log | ⬜ Future feature |
| Services & chat | Front-desk requests, add-ons | ⬜ Future feature |

---

## Mock-Data Strategy

Mock behaviour lives **inside each feature's `data` module**, behind the same domain repository contract a real integration would implement (see [docs/architecture/04-mock-vs-live.md](docs/architecture/04-mock-vs-live.md)):

- `Mock<Feature>DataSource` holds state in a `MutableStateFlow` and emits believable, **time-varying** changes (temperature drift, command latency, event history) — instead of REST calls it imitates subscribing to a room hub / MQTT-style stream.
- Commands apply an in-memory delay + state update, so the UI shows realistic loading → success transitions.
- The `mock` product flavor is already wired in [app/build.gradle.kts](app/build.gradle.kts); the `live` flavor is the production seam.

No persistence, network, PMS auth, BLE, or real hardware is required to run the prototype.

---

## Key Interactions

- Reactive state: Compose screens collect `StateFlow<UiState>` via `collectAsStateWithLifecycle()`.
- One-shot events (snackbars, navigation) use `SharedFlow` (replay = 0).
- One-tap orchestration: an "energy mode" use case sets several device states atomically.

---

## Demo-Critical States

For a convincing demo, the prototype shows: **loading**, **content** (room state at a glance), an **error** snackbar (e.g. toggling the faulty Bathroom light), and **on/off** device states — all driven from mock data. There is no empty state: a hotel room always has controls.

---

## What Is Intentionally Not Built

- No real backend, PMS, BLE lock, or IoT gateway integration (assessment requires none).
- No authentication / real guest identity.
- No persistence across app restarts (in-memory mock state).
- Only one vertical slice is targeted for the prototype; the remaining feature modules are documented, not coded.
- No analytics SDKs, push infrastructure, or offline sync — these are production-evolution items in [TODO.md](TODO.md).

---

## How the Prototype Supports the Product Narrative

The prototype is deliberately **narrow but deep**: one feature, built end-to-end through real architectural boundaries, with a believable mock that proves the production seam (`mock` → `live`) is genuine. That demonstrates the assessment's core claim — that this problem can move from *validated idea* to *prototyped solution* to *ready-for-real-solution* without rewriting the UI or business logic. The breadth of the opportunity lives in the docs; the depth lives in the code.
