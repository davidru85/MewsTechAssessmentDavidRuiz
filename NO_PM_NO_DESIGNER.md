# Working Without a PM or Designer

> How product focus and UX quality are maintained by a solo engineer.
> Deep reference: [spec-07 No PM / No Designer](specifications/spec-07-no-pm-designer.md).

---

## Decision-Making Principles

- **Evidence over opinion:** discovery signals (fake-door scan rate, energy waste data) decide what to build — not personal taste. See [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md).
- **Narrow but deep:** one validated vertical slice beats many shallow features.
- **Defaults over invention:** lean on Material 3 instead of designing a bespoke system.
- **Decisions are logged, not relitigated:** see the ADRs in [docs/decisions/](docs/decisions/).
- **Time-box polish:** a hard cap (e.g. ~1h custom theming/feature) prevents perfectionism from blocking shipping.

---

## Business Alignment

Every feature must map to a measurable outcome — **guest experience** *or* **energy/operational savings**, ideally both ([spec-01](specifications/spec-01-problem-statement.md) dual-KPI rule). If a candidate feature can't be tied to one of those, it doesn't enter the MVP. The adoption metric framework ([ADOPTION_METRICS.md](ADOPTION_METRICS.md)) keeps the build honest about whether it's delivering value.

---

## User Feedback Loops

Without a PM, feedback is gathered directly and cheaply:

- Fake-door / Wizard-of-Oz experiments before building.
- Staff interviews (maintenance, front desk) for operational pain.
- Review mining for guest sentiment.
- In-app telemetry + short checkout survey post-launch.
- Pilot rooms vs. control rooms for causal feedback.

---

## Prioritisation Framework

- **ICE** — score each candidate on Impact (1–10), Confidence (1–10), Ease (1–10); rank by the average; prune the bottom.
- **MoSCoW** — Must / Should / Could / Won't for sprint-level scope, keeping the MVP tight.
- A visible **scope ledger** records what was cut and why (target: ≥30% of considered features deliberately cut), so the discipline is auditable in the demo.

Example: a "TV control" feature with low energy impact and high integration complexity scores poorly on ICE and is cut from the MVP.

---

## UX Quality Checks

- **Material 3 first:** >85% of UI from M3 components; custom Composables only when M3 lacks an equivalent and the need is validated.
- **Dark mode parity:** both themes built and tested for 100% feature coverage.
- **Design tokens in code** (`:core:ui`): color/type/shape/spacing as Kotlin objects — no inline magic numbers ([docs/design/design-tokens.md](docs/design/design-tokens.md)).
- **Visual regression** (Paparazzi/Roborazzi) to catch drift, optional for the prototype.

---

## Accessibility Basics

Baseline enforced from the start ([docs/design/accessibility-baseline.md](docs/design/accessibility-baseline.md)):

- ≥48dp touch targets on every interactive element.
- WCAG 2.1 AA contrast on text/background pairs.
- TalkBack labels and sensible focus order on the primary flow.
- Dynamic font scaling; loading/error/locked/unlocked states announced clearly.

---

## Stakeholder Communication

- **ADRs** capture architectural decisions and their consequences for any future collaborator ([docs/decisions/](docs/decisions/)).
- **Pull requests per feature** are the self-review + demo record, even working solo ([docs/development/branching-and-pull-requests.md](docs/development/branching-and-pull-requests.md)).
- **This documentation suite** is the artifact a hiring manager, future designer, or future PM reads to get aligned without a meeting.
