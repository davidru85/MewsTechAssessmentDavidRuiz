# Detailed Specification for Adoption Plan and Metrics

## Section Overview (Executive Summary)

This section defines how the Smart Guest Room Management system reaches guests and how its success is measured in the wild. The adoption strategy centers on a *zero-friction check-in moment* — leveraging the hotel's captive Wi-Fi portal or an NFC chip on the room key card to onboard guests with minimal effort. The metrics framework spans three layers: user engagement, business outcomes (energy, support load), and guest satisfaction, giving the operations team a holistic view of value delivered.

---

## Core Components & Features

- **Captive Portal Onboarding:** When a guest connects to the hotel Wi-Fi, the captive portal surfaces a deep link to the app (or, for web-only fallback, a PWA-style web control page).
- **NFC-Enabled Room Card:** Physical card with an embedded NFC chip that triggers the same deep link via a tap — covers the case where the guest skips the Wi-Fi flow.
- **QR Code Backup:** A QR code on the nightstand or in-room info card as a third onboarding path.
- **Deep Link Handling:** Android App Links with verified associations, so the tap from NFC/QR lands directly in the app (or Play Store if not installed).
- **First-Run Experience:** A short, skippable tutorial highlighting the top 3 features (thermostat, lights, sleep mode).
- **Engagement Telemetry:** Per-guest usage logs (feature used, time, frequency) tied to reservation IDs.
- **Energy Telemetry:** Per-room kWh logs correlated with occupancy and "app vs. manual control" attribution.
- **Support Load Telemetry:** Work order system exports for thermostat/HVAC tickets.
- **Satisfaction Telemetry:** Post-stay survey with an app-specific question block.
- **Coaching Loop:** In-app nudges (e.g., "Activate Sleep Mode to save energy") at appropriate moments to drive feature discovery.

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Hotel Operations Manager
- **Pain:** New tech launches often fail at the adoption step — guests don't download the app, the dashboard is unused, and the ROI never materializes.
- **Pain:** Needs a clear, measurable rollout funnel from "guest walks in" to "guest uses the app."

### Marketing / Guest Experience Lead
- **Pain:** Wants a hero feature to differentiate the property; needs the onboarding to feel premium, not pushy.

### Guest
- **Pain:** Doesn't want to download yet another app for a 2-night stay; the value must be obvious *and* the friction must be near-zero.
- **Pain:** Doesn't want to be nagged — onboarding nudges should respect attention.

### IT / Network Team
- **Pain:** Captive portal changes can break other systems; the onboarding flow must not interfere with normal Wi-Fi access.

### Revenue Manager
- **Pain:** Wants to tie app usage to incremental revenue (upsells, add-ons).

---

## Functional Requirements (FRs) / User Stories

1. **As a guest arriving at the hotel**, I want to be prompted to open the app the moment I connect to Wi-Fi, **so that** I don't have to search for it.
2. **As a guest**, I want to tap my room card on my phone to launch the app, **so that** onboarding is instant and obvious.
3. **As a guest**, I want a brief, opt-in tutorial that shows me the 3 most useful features, **so that** I feel oriented without being lectured.
4. **As a guest**, I want the app to remember my preferences across the stay, **so that** the second night feels like home.
5. **As a hotel operations manager**, I want a dashboard showing per-room app engagement and energy savings, **so that** I can prove the rollout is working.
6. **As a marketing lead**, I want in-app nudges to surface underused features (e.g., energy modes) without being spammy, **so that** adoption grows organically.
7. **As a revenue manager**, I want to track which add-on purchases originated from the app, **so that** I can quantify the upsell channel.
8. **As a guest**, I want to opt out of the app entirely and still have a working room, **so that** I'm not forced into a tech experience I don't want.

---

## Success Metrics (KPIs)

### Adoption Funnel
| KPI | Target Range | Measurement Method |
|---|---|---|
| Wi-Fi captive portal click-through | >60% | Captive portal logs |
| App install rate (post-prompt) | >40% | Play Store + deep link attribution |
| First feature use within 24h of install | >70% | App telemetry |
| Daily active users during stay | >50% of installs | App telemetry |
| Feature breadth (≥3 features used per stay) | >40% of guests | App telemetry |

### Business Outcomes
| KPI | Target Range | Measurement Method |
|---|---|---|
| kWh reduction per automated room | 20–35% | Energy telemetry + control group |
| Maintenance ticket reduction (thermostat/HVAC confusion) | 40–60% | Work order system |
| Add-on revenue lift (in-app vs. phone baseline) | +10–20% | Revenue attribution |
| Front-desk call reduction (amenity requests) | 25–40% | Telephony analytics |
| Check-out completion via app | >50% | PMS data |

### Guest Satisfaction
| KPI | Target Range | Measurement Method |
|---|---|---|
| App-specific NPS | >40 | In-app survey |
| Overall stay NPS (automated vs. control rooms) | +10% | Post-stay survey |
| "Comfort" satisfaction score | +0.3 to +0.7 | Post-stay survey |
| App store rating | >4.3 | Play Store |

---

## Technical Considerations

- **Captive Portal Integration:** Standard WISPr 2.0 / RFC 7710bis protocols; the portal splash page should deep-link to the app and provide a "skip" path so Wi-Fi is never blocked.
- **NFC Chip Selection:** NTAG215 / NTAG216 with NDEF records containing the deep link URI; cost <$1 per card, programmable via Android NFC tools.
- **App Links Verification:** Use Digital Asset Links (`/.well-known/assetlinks.json`) for verified deep links — prevents app spoofing and improves UX.
- **Telemetry Pipeline:** Anonymous usage events → cloud (e.g., BigQuery / Snowflake) for analysis; per-room energy data flows through the BMS integration.
- **A/B Testing Framework:** Roll out features to a subset of rooms and measure against control rooms — the gold standard for proving causality.
- **Opt-Out Path:** Critical — guests who decline the app must still have working HVAC, lights, etc. via wall controls. The app is an enhancement, not a gate.
- **In-App Nudge Frequency:** Rate-limited (e.g., max 1 nudge / 24h) and dismissible; respect Do-Not-Disturb settings and quiet hours.
- **Survey Strategy:** Keep it short (≤3 questions); trigger at checkout, not during the stay; offer an incentive (loyalty points) for completion.
- **Privacy:** All telemetry must be consent-based post-GDPR / CCPA; a one-time consent prompt at first launch is the cleanest pattern.
