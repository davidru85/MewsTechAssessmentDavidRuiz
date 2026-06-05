# Adoption & Metrics

> How hotels and guests adopt Smart Guest Room Management, and how success is measured.
> Deep reference: [spec-06 Adoption & Metrics](specifications/spec-06-adoption-metrics.md).

---

## Adoption Audience

Two-sided adoption:

- **Hotels (the buyer):** operations managers (energy/ROI), guest-experience/marketing leads (differentiation), IT/network teams (integration safety).
- **Guests (the user):** must get obvious value with near-zero friction for a short stay — and must never be *forced* into the app.

---

## Rollout Strategy

- **Pilot first:** ~10 stratified rooms, automated vs. control, to prove energy + satisfaction deltas before property-wide rollout.
- **Property-by-property:** expand once a property hits pilot thresholds.
- **Feature-flagged per property/market:** stage features without code branches; flags must not create feature-to-feature dependencies.

---

## Onboarding Plan (zero-friction check-in moment)

Three converging, low-effort entry paths:

1. **Captive Wi-Fi portal** → deep-links to the app (web control fallback if not installed). Must never block normal Wi-Fi access ("skip" path always available).
2. **NFC room card** → tap-to-launch (NTAG215/216, <$1/card).
3. **QR code** on the nightstand → third path.

Then a short, **skippable** first-run tour of the top 3 features (thermostat, lights, sleep mode). Verified Android App Links prevent spoofing. **Opt-out is first-class:** a guest who declines still has a fully working room via wall controls.

---

## Success Metrics

### Adoption funnel
| Metric | Target | Source |
|---|---|---|
| Captive-portal click-through | >60% | Portal logs |
| App install after prompt | >40% | Deep-link attribution |
| First feature use within 24h | >70% | App telemetry |
| Daily active during stay | >50% of installs | App telemetry |
| Feature breadth (≥3 features/stay) | >40% | App telemetry |

### Engagement metrics
| Metric | Target | Source |
|---|---|---|
| Smart Energy Mode usage | >40% of guests | App telemetry |
| Keyless entry adoption | >60% | Lock telemetry |
| Push notification open rate | >35% | Push analytics |
| App store rating | >4.3 | Play Store |

### Operational / business metrics
| Metric | Target | Source |
|---|---|---|
| kWh reduction per automated room | 20–35% vs. control | Energy telemetry + control group |
| Maintenance ticket reduction (HVAC confusion) | 40–60% | Work-order system |
| Front-desk call reduction (amenities) | 25–40% | Telephony analytics |
| Add-on revenue lift | +10–20% | Revenue attribution |
| In-app checkout completion | >50% | PMS data |

### Guest satisfaction
| Metric | Target | Source |
|---|---|---|
| App-specific NPS | >40 | In-app survey |
| Overall stay NPS (automated vs. control) | +10% | Post-stay survey |
| Comfort satisfaction | +0.3 to +0.7 | Post-stay survey |

---

## Feedback Loop

- **In-app nudges** (rate-limited, ≤1/24h, dismissible) surface underused features without nagging; respect quiet hours / DND.
- **Short checkout survey** (≤3 questions), incentivised with loyalty points.
- **Operational dashboards** for the hotel: per-room engagement + energy savings.
- **A/B testing** vs. control rooms is the gold standard for causal proof.
- **Consent-based telemetry** only (GDPR/CCPA), one-time prompt at first launch.

---

## Iteration Strategy

1. Measure the funnel and find the biggest drop-off.
2. Rank fixes/features by ICE (see [NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md)).
3. Ship behind a feature flag to a room subset.
4. Compare against control; keep, iterate, or roll back.
5. Feed validated needs into the next vertical slice ([DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md)).
