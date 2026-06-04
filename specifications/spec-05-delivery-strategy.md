# Detailed Specification for Delivery Strategy: MVP vs. Production

## Section Overview (Executive Summary)

This section codifies the dual delivery model: a *Prototype Mode* optimized for speed and demo-ability, and a *Production Mode* that scales to real hotel deployments without rewriting the UI or business logic. The core principle is that the boundary between modes lives entirely in the data layer's repository implementations, which are injected via Hilt. This separation enables rapid iteration on the prototype while preserving a clean path to production — a critical narrative for any tech assessment reviewer evaluating engineering judgment.

---

## Core Components & Features

- **Two-Mode Architecture:** A binary `BuildConfig` flag or build flavor (`useMockDataSource`) controls which repository implementation is injected.
- **In-Memory Prototype State:** All state is held in `MutableStateFlow` instances inside a singleton-scoped `MockRoomStateDataSource` — no persistence, no network, no auth.
- **Mocked Authentication:** Prototype uses a hardcoded "guest" identity with a mock room number; no real PMS integration.
- **Production Data Source Interface:** A clean `RoomStateDataSource` interface with two implementations — `MockRoomStateDataSource` (prototype) and `MqttRoomStateDataSource` (production).
- **Repository Mediator:** Repositories in `:core:data` depend only on the interface; they don't know or care which implementation is active.
- **Use Case Stability:** Domain use cases accept repositories, not data sources — the same use case runs against either implementation.
- **ViewModel Continuity:** ViewModels and UI bind to the same StateFlow contracts regardless of mode; UX behavior is identical.
- **Configuration Strategy:** `MqttRoomStateDataSource` connects to the hotel's gateway broker; handles auth, reconnect, TLS, and topic subscriptions.
- **Migration Checklist:** A documented list of what changes between modes (data source, DI module, network config, logging policy) to support the leap from prototype to production.

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Hiring Manager / Tech Reviewer
- **Pain:** Has seen candidates build prototypes that can't scale; wants evidence the candidate thinks about production from day one.
- **Pain:** Wants to see *where* the seams are between prototype and production — and that they're clean.

### Engineering Lead (future)
- **Pain:** Will need to swap the mock for real; needs that swap to be a 1-day task, not a 1-month rewrite.

### Product Manager
- **Pain:** Wants the demo to feel real, but understands it can't actually control a real room; needs the prototype to be visually and behaviorally convincing.

### Hotel IT (production deployment)
- **Pain:** Needs to understand which network endpoints, credentials, and protocols the production data source will use — without being misled by prototype shortcuts.

### QA / Test
- **Pain:** Wants to be able to test both modes — including simulating network failures in production mode.

---

## Functional Requirements (FRs) / User Stories

1. **As a tech reviewer**, I want the prototype to run end-to-end with zero backend infrastructure, **so that** I can demo it without setup.
2. **As a tech reviewer**, I want the data layer to expose a clean interface, **so that** the production swap is visibly low-risk.
3. **As an engineering lead**, I want Hilt modules to provide distinct bindings for prototype vs. production, **so that** the swap is a configuration change, not a code change.
4. **As a product manager**, I want the prototype to produce believable, time-varying state (e.g., temperature drift), **so that** the demo doesn't feel static.
5. **As a tech reviewer**, I want the same use cases and ViewModels to work in both modes without modification, **so that** I can verify the architectural seam is real, not just claimed.
6. **As a hotel IT operator**, I want the production data source to support standard MQTT brokers with TLS, **so that** it integrates with the existing hotel gateway.
7. **As a QA engineer**, I want to inject a faulty data source to simulate network drops, **so that** I can verify the UI handles errors gracefully.
8. **As an engineering lead**, I want a documented "Prototype → Production" migration checklist, **so that** the team knows exactly what to do when it's time to ship.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| Time to swap data source implementation | <1 hour | Migration checklist exercise |
| Code that requires modification between modes (UI / domain) | 0 lines | Grep / inspection |
| Prototype cold-start to dashboard | <2 seconds | Manual timing |
| Production mode cold-start to dashboard | <3 seconds | Manual timing |
| Memory footprint (prototype) | <80 MB resident | Android Profiler |
| Crash-free sessions | >99.5% in both modes | Crashlytics / Logcat |
| Documented coverage of "what changes between modes" | 100% | Migration checklist |
| Test pass rate across both modes | 100% | CI |

---

## Technical Considerations

- **Interface Design:** `interface RoomStateDataSource { val roomState: StateFlow<RoomState>; suspend fun toggleLight(...); ... }` — designed for both sync (mock) and async (MQTT) implementations.
- **Hilt Qualifiers:** Use `@Qualifier` annotations (`@MockDataSource`, `@ProductionDataSource`) or `@BindsOptionalOf` patterns to keep DI modules clean.
- **Build Variants:** `debug` flavor injects mocks; `release` injects the MQTT-backed source. A `BuildConfig.USE_MOCK_DATA` flag can also drive this without flavors.
- **MQTT Client:** Paho Android Service or HiveMQ MQTT client; consider Eclipse Paho for maximum compatibility. Use TLS and credential-based auth.
- **Topic Structure:** `hotels/{hotelId}/rooms/{roomId}/state` for state push; `hotels/{hotelId}/rooms/{roomId}/cmd` for commands.
- **Reconnection Strategy:** Exponential backoff; persistent session where supported; local state cache during disconnect.
- **Logging:** Prototype logs verbose; production logs at WARN/ERROR with crash reporting (Crashlytics) and structured logs (Timber + remote sink).
- **Feature Flags:** Even in production, individual features should be toggleable remotely (Firebase Remote Config or equivalent) for staged rollout and kill switches.
- **Migration Checklist (high level):** Replace mock data source → wire MQTT client → integrate with PMS for auth → add telemetry pipeline → harden error handling → enable crash reporting → pen test.
