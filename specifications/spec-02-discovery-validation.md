# Detailed Specification for Discovery and Validation

## Section Overview (Executive Summary)

This section operationalizes the principle that product decisions must be evidence-driven before code is written. It defines a three-tier validation strategy — quantitative telemetry analysis, qualitative field research, and a low-cost "fake door" experiment — that together produce statistically and behaviorally grounded conviction about the problem and the willingness of guests to use a phone-based control app. The output of this phase is a go/no-go decision backed by data, not assumptions.

---

## Core Components & Features

- **Energy Consumption Profiling Tool:** Cross-reference hotel kWh curves with check-in/check-out timestamps and presence sensor logs to identify baseline waste windows.
- **Maintenance Knowledge Capture:** Structured interviews with the maintenance team to surface recurring "fix the thermostat" tickets and their root causes.
- **Review Mining Pipeline:** NLP-assisted scan of online reviews (Booking, TripAdvisor, Google) for keywords related to room temperature, comfort, and HVAC confusion.
- **Fake Door / Smoke Test Infrastructure:** QR code + landing page setup for pilot rooms; logs scan intent without requiring a working app.
- **NFC Tag Variant:** Physical NFC chip alternative for guests who prefer tap-to-prompt over QR scan.
- **Conversion Tracking:** Backend logs the scan event, time, room number, and (optional) follow-through signal to validate demand.
- **Pilot Room Selection Criteria:** Methodology for choosing 10 statistically representative rooms (floor, view, room type) for the test.
- **Validation Report Template:** Standardized output documenting findings, signals strength, and recommended next step (build / iterate / drop).

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Hotel Operations Manager
- **Pain:** No current methodology to measure guest demand for new tech — historically, decisions are made on vendor pitches, not data.
- **Pain:** Needs defensible ROI projections before approving capex or opex commitments.

### Maintenance Team Lead
- **Pain:** Recurrent "user error" tickets are not formally tracked — the cost of confusion is invisible.
- **Pain:** Wants a structured way to surface pain points without writing reports.

### Revenue / Marketing Manager
- **Pain:** Brand differentiators (smart room, mobile control) are often claimed but rarely substantiated.
- **Pain:** Needs validation evidence to support pricing or marketing claims.

### Guest (indirect stakeholder during discovery)
- **Pain:** Their frustration with in-room tech is currently expressed only as 1-star reviews or complaints to the front desk — never captured in a structured way.

### Product / Engineering Team
- **Pain:** Risk of building a product no one uses; needs pre-build demand signal.

---

## Functional Requirements (FRs) / User Stories

1. **As a product manager**, I want to quantify the energy waste per unoccupied room, **so that** I can build a defensible business case before any development begins.
2. **As a research lead**, I want to interview the maintenance team and capture recurring pain points, **so that** I can prioritize features that address real, frequent issues.
3. **As a product manager**, I want to scrape and analyze guest reviews for comfort-related complaints, **so that** I can corroborate quantitative findings with qualitative signals.
4. **As a growth / product marketer**, I want to run a fake-door test with 10 pilot rooms using QR codes, **so that** I can measure real demand at near-zero engineering cost.
5. **As a guest pilot participant**, I want to scan a QR code on my nightstand and land on a clear, single-purpose page, **so that** my intent to use a smart-room app is captured in seconds.
6. **As a hotel operations manager**, I want a clear go/no-go report with quantified signals, **so that** I can decide whether to fund the build.
7. **As an engineering lead**, I want discovery outputs to include a ranked list of validated user needs, **so that** I can scope the MVP to only what the data supports.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| QR / NFC scan rate in pilot rooms | >25% of guests scan | Scan logs vs. occupancy |
| Landing-page-to-beta-signup conversion | >40% of scanners express intent | Landing page form / button |
| Maintenance tickets mentioning "thermostat" or "AC" | Establish baseline + 30%+ reduction in pilot rooms post-launch | Work order system |
| Negative review mentions of "room temperature" / "HVAC" | Identify baseline rate (e.g., 8–12% of reviews) | Review NLP pipeline |
| kWh waste window identified | Quantify unoccupied/over-conditioned hours per room | Energy + occupancy cross-ref |
| Validation report sign-off | 100% of stakeholders (ops, maintenance, GM) sign off | Document workflow |
| Time from discovery start to build decision | <4 weeks | Project tracking |

---

## Technical Considerations

- **Data Sources:** PMS (Property Management System) for check-in/out, BMS for energy, IoT sensors for presence, review APIs (or scrapers) for online sentiment.
- **Analytics Stack:** Lightweight — a Jupyter notebook or BI tool is sufficient for the discovery phase. No production infra needed yet.
- **Fake Door Page:** A simple static landing page (HTML/JS) with a "Notify me" CTA; hosted on a cheap cloud instance or even a Notion/Linktree equivalent. Must log anonymous scan events with a room-ID tag.
- **NFC Tag Provisioning:** Standard NTAG215/216 chips, pre-programmed with the deep link; cost is <$1 per room.
- **Privacy Compliance:** Even at the discovery stage, scans should not capture PII without consent — especially in EU markets (GDPR).
- **Interview Tooling:** Structured interview template with consistent scoring rubric to avoid interviewer bias.
- **Review Mining:** Keyword set should be locale-aware (e.g., "aire acondicionado" in Spanish reviews); consider translation pipelines if operating in multi-language markets.
- **Pilot Room Sampling:** Stratified sampling ensures results generalize — don't pick 10 rooms on one floor or one room type.
