# TODO — Out-of-Scope & Future Work

> Scope boundary: this file captures work **outside** the take-home assessment scope so the prototype stays focused. Items here are deliberately *not* required to satisfy the assessment (see [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md)). They are the honest backlog, not gaps that undermine the submission.

---

## Assessment-Critical Gaps (do before/with the demo)

These are the only items needed to fully satisfy the assessment's prototype requirement:

- [ ] Implement the first **vertical slice** (recommended: Room Controls / Dashboard) end-to-end: `:feature:controls:domain` → `:data` (mock) → `:presentation` → `:app` wiring.
- [ ] Believable, time-varying `MockControlsDataSource` (latency + temperature drift).
- [ ] Demo-critical states: loading, success, empty, error.
- [ ] Tests for the slice: use-case (JUnit), ViewModel state (Turbine), one Compose smoke test.
- [ ] Wire the Dashboard into the root `NavHost` (replace the placeholder in [AppRoot.kt](app/src/main/java/com/mews/guestroom/AppRoot.kt)).

---

## Future Product Improvements

- [ ] Additional feature slices by validated priority: Access (keyless entry), Services & chat, Notifications, Profile/Info.
- [ ] Smart Energy Modes beyond Sleep (Away, Welcome, Wake-up) with manual override + auto-revert.
- [ ] Multi-language (i18n) and richer accessibility settings.
- [ ] In-app nudges and feature-discovery coaching.

---

## Future Technical Improvements

- [ ] Live data sources per feature (MQTT/BLE/PMS) behind existing domain contracts.
- [ ] Persistence layer (only when a feature genuinely needs it).
- [ ] Convention plugins in `build-logic/` to dedupe module Gradle config.
- [ ] Static-analysis CI gates: reject Android imports in `:feature:*:domain`, reject cross-feature deps (ktlint/detekt).
- [ ] Visual regression (Paparazzi/Roborazzi) and a `:core:testing` module.

---

## Production Hardening Ideas

- [ ] Reservation-window authorization (control auto-revokes at checkout).
- [ ] Offline / degraded-mode behaviour for critical controls (lock, climate).
- [ ] Privacy-safe telemetry + audit-log export; consent + checkout data purge (GDPR/CCPA).
- [ ] Observability: crash reporting, structured logging, energy-savings attribution.
- [ ] Security review for BLE / MQTT / gateway communication; rolling-token keyless entry.
- [ ] Zero-friction onboarding infra: captive portal deep links, verified App Links, NFC card provisioning.

---

## Optional `api/impl` Migration Candidates

Apply **only** when a stable public contract is genuinely needed — never by default ([ARCHITECTURE.md](ARCHITECTURE.md), [ADR-0006](docs/decisions/0006-api-impl-when-needed.md)):

- [ ] A feature consumed by multiple other features or owned by a separate team.
- [ ] `:app` needing a feature entry point without seeing internal screens/ViewModels/data sources.
- [ ] A feature large enough that compile-time isolation matters.

Preferred shape when triggered: `:feature:<name>:api` + `:domain` + `:data` + `:presentation` + `:impl`. Must not reintroduce shared `:core:domain` / `:core:data`.
