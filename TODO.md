# TODO — Out-of-Scope & Future Work

> Scope boundary: this file captures work **outside** the take-home assessment scope so the prototype stays focused. Items here are deliberately *not* required to satisfy the assessment (see [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md)). They are the honest backlog, not gaps that undermine the submission.

---

## Assessment-Critical Gaps (do before/with the demo)

The prototype slice is built — these items are now complete:

- [x] Implement the first **vertical slice** (Room Controls / Dashboard) end-to-end: `:feature:controls:domain` → `:data` (mock) → `:presentation` → `:app` wiring.
- [x] Believable, time-varying `MockControlsDataSource` (latency + temperature drift + faulty/unknown-device errors).
- [x] Demo-critical states: loading, content, error (empty intentionally dropped — a room always has controls).
- [x] Tests for the slice: 18 JVM unit tests (use-case, MockDataSource, ViewModel via Turbine). Compose smoke test deferred (needs emulator/Robolectric).
- [x] Wire the Dashboard into the root `NavHost` (placeholder replaced).

Remaining before the live session: rehearse [DEMO_SCRIPT.md](DEMO_SCRIPT.md).

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
- [x] Static-analysis CI gates: ktlint + detekt on all modules, plus an architecture check (`scripts/check-architecture.sh`) that rejects Android/Hilt imports in `:feature:*:domain` and cross-feature imports. See [CI_CD.md](CI_CD.md).
- [ ] Instrumented/Compose UI tests + coverage reporting in CI (needs first feature slice / emulator).
- [ ] Visual regression (Paparazzi/Roborazzi) and a `:core:testing` module.
- [ ] **Mock/live by product flavor** in `:feature:controls:data` (P1.2): mock is bound in `main` today; a `live` source set + binding land in Phase 2 when a real data source exists.
- [ ] Replace the grep-based architecture guard with a Gradle module-dependency rule (Konsist / module-graph); `scripts/check-architecture.sh` is a cheap pre-check only.
- [ ] Scope detekt `MagicNumber` to presentation / `:core:ui` rather than disabling it repo-wide.

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
