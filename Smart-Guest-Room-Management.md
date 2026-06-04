# Smart Guest Room Management — Project Overview

This document consolidates the product vision, technical approach, and feature roadmap for a system that lets guests manage their hotel stay — room controls, communication with staff, and booking extra services — all from their phone.

---

## 1. The Problem: In-Room Friction and Energy Waste

**The goal is to solve a dual problem: the user's experience and the business's ROI.**

**For the guest (User Pain):** Hotel rooms typically have confusing HVAC controls, unintuitive light switches, and isolated systems. Often the room key card is left in the slot simply to keep devices charging, leaving lights on unnecessarily.

**For the hotel (Business Pain):** HVAC and electricity spend in empty rooms is one of the largest operational costs.

**The current opportunity:** Hotels are already installing Bluetooth locks and sensor networks (Zigbee/Matter), but interaction is limited to maintenance staff. Exposing simplified local control directly to the guest's phone democratizes that infrastructure.

---

## 2. Discovery and Validation

**Show that we don't start coding blindly — we validate impact first.**

**Quantitative Signals:** Analyze the hotel's electricity consumption curve (kWh) cross-referenced with check-in/check-out data from presence sensors or door locks.

**Qualitative Signals:** Interview the maintenance team (how often do they have to fix/explain a thermostat?) and read online reviews looking for mentions of room temperature or comfort.

**Lightweight Experiment (Fake Door Test):** Place a QR code (or an NFC tag) on the nightstand of 10 pilot rooms reading: *"Control your room from your phone."* When scanned, the user lands on a web page indicating the service is "in beta" and logs the intent to use it. If the scan rate is high, we have validation at zero development cost.

---

## 3. Feature Overview

### Room Controls & Automation

Guests can adjust their environment directly from the app — no need to hunt for obscure wall panels.

- **AC & Heating:** Full control over the room's climate control system.
- **Lighting:** Turn lights on and off from anywhere in the room.
- **Blinds/Shades:** Automatic control to open or close window coverings.
- **Smart Energy Modes:** One-tap presets like "Sleep Mode" that turn off lights, lower the blinds, and set the thermostat to an energy-efficient temperature.

### Security & Access Control

- **Keyless Entry:** Unlock the room door by bringing the guest's phone close to it (Bluetooth/NFC).
- **Timed Access Permissions:** Room control automatically cuts off once the guest's checkout time passes.
- **Staff Tracking:** An access system that requires cleaning staff or other employees to log in and identify themselves before opening the door.

### Notifications & Alerts

- **Away-from-Room Monitoring:** Alerts to guests if activity occurs in their room while they're out (e.g., housekeeping entry).
- **Checkout Reminder:** Automated message sent the day before departure reminding guests of checkout time.

### Chat & Hotel Services

- **Front Desk Requests:** Order room service, breakfast, request extra towels and amenities — all from within the app.
- **Add-ons & Extras:** Quick booking of hotel perks like parking, luggage storage, or transportation.
- **Automated Quick Requests:** One-tap actions for common guest needs.

### General Info & Profile

- **Hotel Facilities:** Pool hours, dining times, gym schedules — all searchable.
- **Guest Profile:** A private area to review personal details, reservation info, and preferences.

---

## 4. The Android Prototype: Architecture and Craft

**Demonstrate technical excellence in Kotlin, with focus on prototyping speed.**

### Technical Approach

The app simulates a local control ecosystem. Instead of traditional REST calls, the data layer simulates subscribing to a messaging broker (MQTT-style), emitting real-time state updates.

### Architecture (Clean + MVVM)

| Layer | Details |
|---|---|
| **Data** | Repository using `StateFlow` or `SharedFlow` with Kotlin Coroutines to emit simulated changes (e.g., ambient temperature gradually drops while AC is on). |
| **Domain** | Clear use cases: `ToggleLightUseCase`, `SetTemperatureUseCase`, `ActivateSleepModeUseCase`, etc. |
| **UI** | Jetpack Compose screens that observe state reactively. |

### Core Flow to Showcase

A **Dashboard** screen displaying:
- Overall room state at a glance
- Quick toggles for energy modes (Sleep Mode, Away Mode, etc.)
- A dial or slider for thermostat control
- One-tap access to individual room controls

---

## 5. Delivery Strategy: MVP vs. Production

**Balance delivery speed with future maintainability.**

**Prototype Mode:** All state lives in memory on the device. Zero backend infrastructure. Dependency injection configured to inject mocks for immediate iteration.

**Production Mode:** Because of Clean Architecture's layered separation, scaling to production means creating a new repository implementation (to connect to the hotel's backend or IoT gateway) and injecting it — the UI and business logic remain untouched.

---

## 6. Adoption Plan and Metrics

**A brilliant app is worthless if no one uses it.**

**Key Moment — Check-in:** The strategy is *zero friction*. When the user connects to the hotel Wi-Fi (Captive Portal), they're prompted to open the app. Alternatively, the physical room card includes an NFC chip for instant pairing.

**Success Metrics:**
- **Engagement:** % of guests who use the app at least once a day.
- **Business:** Estimated kWh reduction per automated room vs. a standard room.
- **Support Reduction:** Decrease in maintenance calls about thermostat confusion.

---

## 7. Working Without a PM or Designer

**Maintain focus and visual quality without dedicated support.**

- **Prioritization:** Use an ICE matrix (Impact, Confidence, Ease) or MoSCoW. If a feature has low impact on energy savings and high technical complexity, it's dropped from the MVP.
- **UX/UI Quality:** Rely strictly on **Material Design 3** components and guidelines. Use automatic tonal color palettes, ensure native *Dark Mode* support, and prioritize accessibility (touch target sizes, contrast) instead of inventing a complex visual design from scratch.

---

## 8. AI-First Development

**Leverage AI as a force multiplier throughout the project.**

- Rapid generation of the entire folder structure and architecture boilerplate.
- Creation of mock data scripts (JSONs simulating complex room configurations).
- Assistance designing specific UI components in Jetpack Compose by passing functional descriptions.
- AI-assisted code review and documentation generation.

---

*Document consolidated from brainstorm01.md and brainstorm02.md*
