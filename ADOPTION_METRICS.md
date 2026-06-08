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

Metrics form **one unified adoption funnel** with three sequential phases. Thresholds differ by phase because each measures a different mechanism at a different maturity — they are stages of the *same* funnel, not competing numbers.

| Phase | When | Mechanism | Headline gate |
|---|---|---|---|
| **1 — Validation** | Pre-build | Cold nightstand QR/NFC fake-door | Scan >25% of guests · landing→intent >40% of scanners (see [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md)) |
| **2 — Pilot (~10 rooms)** | Post-build, pre-rollout | Automated vs. control rooms | Measurable energy + satisfaction delta vs. control (gate to scale) |
| **3 — Adoption (scale)** | Post-launch, property-wide | Warm captive-portal / NFC onboarding | Portal click-through >60% · install after prompt >40% |

> **Why Phase 1 (>25%) and Phase 3 (>60%) differ:** Phase 1 is a *cold* demand signal from an unfamiliar QR with no product behind it; Phase 3 is a *warm* captive-portal tap at check-in on an already-launched property. Different stages and baselines — not a contradiction.

### Phase 2 — Pilot (~10 rooms)

The bridge between fake-door validation and property-wide rollout: ~10 stratified rooms, automated vs. control, gated on a measurable delta before scaling ([ROADMAP.md](ROADMAP.md) Phase 2). The pilot reuses the operational/satisfaction targets below as its go/no-go gate:

| Pilot gate | Target | Source |
|---|---|---|
| kWh reduction per automated room | 20–35% vs. control | Energy telemetry + control group |
| Comfort satisfaction delta | +0.3 to +0.7 | Post-stay survey |
| Overall stay NPS (automated vs. control) | +10% | Post-stay survey |
| Rollout decision | explicit go / iterate / hold | Pilot review |

### Phase 3 — Adoption funnel (post-launch, scale)
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
