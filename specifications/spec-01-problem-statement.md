# Detailed Specification for The Problem: In-Room Friction and Energy Waste

## Section Overview (Executive Summary)

This section establishes the dual-purpose thesis of the Smart Guest Room Management system: solving a tangible *guest experience* problem (friction with in-room technology) while unlocking a quantifiable *business outcome* (energy waste reduction). The opportunity is rooted in the fact that hotels have already deployed IoT infrastructure — Bluetooth locks, Zigbee/Matter sensor networks — but expose it only to maintenance staff, leaving guests to interact with legacy wall panels. By democratizing that infrastructure through a phone app, the project addresses both pains with a single product.

---

## Core Components & Features

- **Problem Domain Mapping:** Explicit articulation of two parallel pain streams (UX and ROI) and their causal link — poor controls cause higher energy waste because guests leave devices running "just in case."
- **Guest Friction Inventory:** Documentation of common in-room pain points — confusing HVAC thermostats, ambiguous light switch banks, key-card slots repurposed as power switches, isolated subsystems that don't communicate.
- **Hotel Cost Modeling:** Identification of HVAC and lighting as primary drivers of energy OPEX in empty or partially-occupied rooms.
- **Infrastructure Audit Capability:** Reuse of *existing* Bluetooth lock and Zigbee/Matter sensor investments rather than greenfield hardware procurement — a critical enabler for hotel buy-in.
- **Opportunity Framing:** Position the mobile app as a *consumer* of the hotel's existing infrastructure, not a parallel system — minimizing CAPEX, accelerating deployment.
- **Dual KPI Framework:** Every feature must be measurable against either (a) guest experience improvement or (b) energy/business savings — and ideally both.
- **Stakeholder Pain Linkage:** Map each pain point to the stakeholder who bears it (guest → comfort/review score; hotel → OPEX/brand reputation).

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Primary Guest (Business Traveler, 28–55)
- **Pain:** Wall-mounted thermostats have cryptic icons, no clear "off" path; lighting scenes require trial-and-error; key card slots force a binary power state.
- **Pain:** Cannot fine-tune environment from bed, the desk, or the bathroom; must physically walk to the panel.
- **Pain:** Inconsistent experience across hotel brands — muscle memory from one property fails at another.

### Leisure Guest / Family
- **Pain:** Multiple climate zones (e.g., a child uncomfortable with the room temperature) require awkward compromise.
- **Pain:** Front desk calls during off-hours feel disruptive; a self-service app would be preferred.

### Hotel Operations Manager
- **Pain:** HVAC and lighting account for ~40–60% of room-level energy spend; reducing this directly impacts NOI.
- **Pain:** Energy waste is invisible — no per-room telemetry to identify which rooms are bleeding money.
- **Pain:** Sustainability reporting is increasingly required by corporate buyers and brand standards (e.g., LEED, BREEAM).

### Maintenance Team
- **Pain:** Recurrent calls about "the AC doesn't work" when in fact the guest simply doesn't understand the panel.
- **Pain:** High support load from preventable confusion, reducing time for actual maintenance.

### Property / Asset Manager
- **Pain:** CAPEX justification for IoT is hard; the same hardware should also drive guest revenue and satisfaction, not just ops.
- **Pain:** Brand inconsistency across properties dilutes loyalty metrics.

---

## Functional Requirements (FRs) / User Stories

1. **As a business traveler**, I want to control the room temperature, lights, and blinds from my phone, **so that** I can adjust my environment without leaving the bed or desk.
2. **As a guest**, I want a single "Goodnight" button that turns off all lights, closes the blinds, and sets the AC to a sleep-friendly temperature, **so that** I don't have to interact with three different panels before sleeping.
3. **As a hotel operations manager**, I want per-room energy telemetry linked to occupancy (check-in / check-out) data, **so that** I can quantify waste and prioritize retrofits.
4. **As a maintenance technician**, I want guest-side confusion calls to be reduced via self-service in-room controls, **so that** my team can focus on actual maintenance tickets.
5. **As a property manager**, I want the in-room IoT infrastructure I already paid for (Bluetooth locks, Zigbee sensors) to be exposed to guests, **so that** the same hardware investment drives both operational efficiency and guest satisfaction.
6. **As a sustainability officer**, I want accurate kWh-saved-per-room metrics, **so that** I can produce credible reports for corporate clients and brand certifications.
7. **As a guest arriving late at night**, I want to set my preferred room temperature before entering, **so that** the room is comfortable the moment I walk in.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| Per-room HVAC energy reduction | 20–35% vs. control rooms | kWh telemetry cross-referenced with occupancy logs |
| Per-room lighting energy reduction | 15–25% vs. control rooms | Smart meter data, occupancy-correlated |
| Guest satisfaction score (comfort) | +0.3 to +0.7 NPS points | Post-stay survey, control vs. automated rooms |
| Maintenance ticket reduction (thermostat/lighting confusion) | 40–60% | Work order system analytics |
| App-driven automation adoption | >60% of guests use at least one feature | In-app telemetry |
| Time to comfortable room temperature | <10 minutes from check-in | Climate telemetry vs. check-in timestamp |
| Sustainability reporting coverage | 100% of automated rooms | Building management system export |

---

## Technical Considerations

- **IoT Protocol Stack:** Likely a mix of Zigbee 3.0 and/or Matter for in-room devices (lights, blinds, thermostats) and Bluetooth Low Energy (BLE) for the door lock and phone-as-key flow. A translation gateway (e.g., a hotel-room hub) bridges these into a single management plane.
- **Backend Telemetry Pipeline:** Time-series data (kWh, temperature, occupancy) requires an event-streaming architecture (Kafka, MQTT broker, or cloud-native equivalent) feeding a metrics store (InfluxDB, TimescaleDB, or cloud time-series).
- **Real-Time State Sync:** The phone app must reflect the actual room state (e.g., the guest thinks the light is off, but a previous guest turned it on). This implies a persistent WebSocket or MQTT push channel, not just request/response.
- **Energy Analytics Layer:** A separate analytics service correlates occupancy events, weather data, and HVAC/lighting telemetry to compute savings — this is the proof point for ROI justification.
- **Privacy & Data Minimization:** Telemetry should be aggregated to the room level for the business; guest-identifiable data should be purged at checkout.
- **Legacy Infrastructure Compatibility:** Many hotels have proprietary BMS (Building Management Systems). The solution should integrate via standard protocols (BACnet, Modbus) or vendor APIs to avoid rip-and-replace.
- **Offline & Degraded Modes:** If the cloud backend is unreachable, in-room control must still work locally — this is a hard requirement for guest trust.
