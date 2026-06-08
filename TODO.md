# TODO — Out-of-Scope & Future Work

> Scope boundary: this file captures work **outside** the take-home assessment scope so the prototype stays focused. Items here are deliberately *not* required to satisfy the assessment (see [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md)). They are the honest backlog, not gaps that undermine the submission.

---

## Assessment-Critical Gaps (do before/with the demo)

The prototype slice is built — these items are now complete:

- [x] Implement the first **vertical slice** (Room Controls / Dashboard) end-to-end: `:feature:controls:domain` → `:data` (mock) → `:presentation` → `:app` wiring.
- [x] Believable, time-varying `MockControlsDataSource` (latency + temperature drift + faulty/unknown-device errors).
- [x] Demo-critical states: loading, content, error (empty intentionally dropped — a room always has controls).
- [x] Tests for the slice: 33 JVM unit tests (use-case, MockDataSource, ViewModel via Turbine). Compose smoke test deferred (needs emulator/Robolectric).
- [x] Wire the Dashboard into the root `NavHost` (placeholder replaced).

Remaining before the live session: rehearse [DEMO_SCRIPT.md](DEMO_SCRIPT.md).

---

## Future Product Improvements

- [ ] Additional feature slices by validated priority: Access (keyless entry), Services & chat, Notifications, Profile/Info.
- [ ] Smart Energy Scenes beyond the shipped Sleep / Away / Welcome (e.g. Wake-up) with manual override + auto-revert.
- [ ] Multi-language (i18n) and richer accessibility settings.
- [ ] In-app nudges and feature-discovery coaching.
- [ ] **Dashboard a11y pass** (from code review): merge each `LightsCard` row into a single `Role.Switch` click target (one TalkBack swipe per light), and attach a polite `liveRegion` to the busy indicator so command start/finish is announced.
- [ ] **Explicit `Disconnected` dashboard state** (live-flavor option 2): replace the live stub's neutral snapshot with a first-class `DashboardUiState.Disconnected` rendered as a clear "not connected" screen, once the live hub work begins.

---

## Future Technical Improvements

- [ ] Live data sources per feature (MQTT/BLE/PMS) behind existing domain contracts.
- [ ] Persistence layer (only when a feature genuinely needs it).
- [ ] Convention plugins in `build-logic/` to dedupe module Gradle config.
- [x] Static-analysis CI gates: ktlint + detekt on all modules, plus an architecture check (`scripts/check-architecture.sh`) that rejects Android/Hilt imports in `:feature:*:domain` and cross-feature imports. See [CI_CD.md](CI_CD.md).
- [ ] Instrumented/Compose UI tests + coverage reporting in CI (needs first feature slice / emulator).
- [x] **Debounce the Climate slider** (from code review): `ClimateCard` holds local drag state and dispatches `onTargetTemperatureChange` only on `onValueChangeFinished`, so a drag no longer spawns one command (350 ms each) per frame.
- [ ] Visual regression (Paparazzi/Roborazzi) and a `:core:testing` module.
- [x] **Mock/live by product flavor** in `:feature:controls:data` (P1.2): the `dataSource` flavor dimension now binds `MockControlsDataSource` (`src/mock`) vs. `LiveControlsDataSource` (`src/live`) via flavor-specific Hilt modules — the bound source is chosen by build flavor with 0 changes outside the feature. The live source is an honest stub (neutral disconnected snapshot + `NOT_CONNECTED` command errors) until the real hub lands.
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
