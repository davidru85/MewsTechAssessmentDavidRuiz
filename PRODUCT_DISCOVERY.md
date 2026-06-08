# Product Discovery

> Detailed source material: [spec-01 Problem Statement](specifications/spec-01-problem-statement.md) and [spec-02 Discovery & Validation](specifications/spec-02-discovery-validation.md).

---

## Problem Statement

Hotel rooms create friction for guests and waste money for operators at the same time:

- **Guest pain:** Confusing HVAC controls, unintuitive light-switch banks, and isolated subsystems. Key cards are left in the slot just to keep power on, so lights and AC run in empty rooms.
- **Hotel pain:** HVAC and lighting are among the largest room-level operating costs (~40–60% of room energy spend), and waste in empty rooms is largely invisible without per-room telemetry.

The two pains share a root cause: the room's technology is hard to control, so guests over-consume "just in case." Solving the guest experience and the energy bill is therefore the *same* product.

---

## Target Personas

| Persona | Who they are | Core pain |
|---|---|---|
| **Business traveler (28–55)** | Frequent guest, values quiet self-service | Cryptic wall panels; can't adjust from bed/desk; inconsistent across brands |
| **Leisure guest / family** | Multi-person stay | Coordinating comfort + requests via front-desk calls is tedious |
| **Hotel operations manager** | Owns OPEX and sustainability reporting | Energy waste is invisible; no per-room telemetry; ROI hard to prove |
| **Maintenance team** | Fixes/explains room tech | Recurrent "the AC doesn't work" calls that are really UX confusion |
| **Property / asset manager** | Justifies CAPEX | Wants existing IoT hardware to drive guest value, not just ops |

---

## Jobs To Be Done

- *When I arrive tired,* I want the room comfortable and controllable from my phone, *so I don't fight a wall panel.*
- *When I go to sleep,* I want one action to dim lights, close blinds, and set a sleep temperature.
- *When I run the property,* I want to cut energy waste in empty rooms without degrading guest comfort.
- *When I report sustainability,* I want credible per-room kWh-saved numbers.

---

## Why Now

Hotels are **already** installing Bluetooth locks and Zigbee/Matter sensor networks, but interaction is limited to staff. The infrastructure cost is largely sunk. A phone app that *consumes* this existing infrastructure unlocks guest-facing value with minimal new CAPEX — a far easier sell than a greenfield hardware program. Rising energy costs and corporate sustainability mandates (LEED/BREEAM, corporate travel buyers) add urgency.

---

## Assumptions

1. Guests are willing to use a phone for room control during a short stay **if friction is near zero**.
2. Hotels have, or can bridge to, controllable in-room devices (thermostat, lights, blinds, lock).
3. Energy savings from occupancy-aware automation are measurable against control rooms.
4. A meaningful share of "AC broken" maintenance tickets are actually UX confusion.

---

## Validation Plan

A three-tier, evidence-before-code approach:

1. **Quantitative telemetry** — cross-reference hotel kWh curves with check-in/out timestamps and presence sensors to quantify the waste window per room.
2. **Qualitative field research** — structured interviews with maintenance/front-desk staff; NLP review-mining (Booking, TripAdvisor, Google) for comfort/temperature/HVAC complaints.
3. **Fake-door experiment** — QR code / NFC tag on the nightstand of ~10 stratified pilot rooms: *"Control your room from your phone."* It lands on a "beta" page and logs intent. High scan rate ⇒ validated demand at near-zero engineering cost.

---

## Lightweight Experiments

- **Fake-door QR/NFC** smoke test (above) — primary demand signal.
- **Concierge/Wizard-of-Oz** — staff manually action a few "app requests" to test desirability before automating.
- **Staff pilot** — give housekeeping/maintenance the audit-log view first to validate operational value.
- **Limited hotel pilot** — automated rooms vs. control rooms to measure energy + satisfaction deltas.

---

## Success and Failure Signals

| | Signal | Threshold |
|---|---|---|
| ✅ Success | QR/NFC scan rate in pilot rooms | >25% of guests |
| ✅ Success | Landing-page → beta-intent conversion | >40% of scanners |
| ✅ Success | Identified kWh waste window per empty room | Quantifiable & material |
| ✅ Success | Reviews mentioning temperature/HVAC discomfort | Material baseline (e.g. 8–12%) |
| ❌ Failure (invalidates) | Scan rate negligible despite clear signage | Guests don't want phone control |
| ❌ Failure | No measurable energy waste window | Business case collapses |
| ❌ Failure | Comfort complaints essentially absent | Guest pain is not real/frequent |

The discovery phase ends in an explicit **go / iterate / drop** decision backed by these signals, not assumptions.

> These thresholds are **Phase 1 (Validation)** of the unified adoption funnel in [ADOPTION_METRICS.md](ADOPTION_METRICS.md). They measure *cold* demand before any product exists; the **Pilot** (~10 rooms) and **Adoption** (post-launch) phases that follow use different mechanisms and higher baselines, so the numbers are sequential stages — not in conflict.

---

## Risks and Mitigations

| Risk | Mitigation |
|---|---|
| Guests won't download an app for a 2-night stay | Zero-friction onboarding (captive portal / NFC card); web fallback; app is an *enhancement*, never a gate |
| Fragmented hotel IoT / legacy BMS | Integrate via standard protocols (Matter/Zigbee/BLE, BACnet/Modbus) behind a per-feature data-source seam |
| Privacy / GDPR concerns | Room-level aggregation, consent at first launch, purge guest-identifiable data at checkout |
| Energy savings unprovable | A/B against control rooms from day one; instrument attribution (app vs. manual) |
| Over-building before validation | Fake-door test gates the build; MVP is a single vertical slice |
