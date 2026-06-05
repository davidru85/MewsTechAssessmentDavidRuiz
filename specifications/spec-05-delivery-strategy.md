# Detailed Specification for Delivery Strategy: MVP vs. Production

## Section Overview (Executive Summary)

This section codifies the dual delivery model: a *Prototype Mode* optimized for
speed and demo-ability, and a *Production Mode* that can evolve toward real
hotel deployments without rewriting feature presentation or feature domain
logic.

The architectural rule is feature isolation. The prototype/production boundary
is owned by each feature's `data` module, not by a shared global data layer.
For example, a selected feature swaps `Mock<Feature>DataSource` for a live
implementation inside `:feature:<name>:data`, while
`:feature:<name>:domain` and `:feature:<name>:presentation` stay stable.

---

## Core Components & Features

- **Two-Mode Architecture Per Feature:** Build flavor or DI configuration
  selects mock/live implementations inside each feature data module.
- **In-Memory Prototype State:** Mock feature data sources hold state in
  `MutableStateFlow` and emit believable device changes without persistence,
  network, PMS auth, BLE, or real room hardware.
- **Feature-Owned Repository Contracts:** Repository interfaces live in the
  owning feature domain module. They are implemented only by that feature's data
  module.
- **Feature-Owned Data Sources:** Mock and live data sources live next to the
  feature repository implementation. There is no shared `:core:data`.
- **Use Case Stability:** Feature use cases accept feature repository
  interfaces, not data sources. The same use case runs in prototype and
  production mode.
- **ViewModel Continuity:** Feature ViewModels and Compose screens bind to the
  same feature-domain contracts in both modes.
- **Configuration Strategy:** Production integrations such as MQTT, BLE, PMS,
  or staff identity are introduced behind feature data-source interfaces.
- **Migration Checklist:** Each feature documents what changes between mock and
  production mode: data source, DI binding, credentials, telemetry, and failure
  handling.
- **Vertical Slice Bias:** The MVP should complete one selected feature
  end-to-end before broadening the surface area.

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Hiring Manager / Tech Reviewer
- **Pain:** Has seen prototypes that claim Clean Architecture but centralize all
  business logic in a shared module.
- **Pain:** Wants evidence that the candidate can build an isolated feature from
  UI to data source.

### Engineering Lead
- **Pain:** Needs to replace a mock integration with a real one without touching
  unrelated features.
- **Pain:** Wants teams to own feature modules independently.

### Product Manager
- **Pain:** Wants the demo to feel real while accepting that it cannot control
  real hotel hardware yet.

### Hotel IT
- **Pain:** Needs clarity about which production integrations belong to which
  feature: BLE lock access, MQTT room state, PMS reservation windows, etc.

### QA / Test
- **Pain:** Wants to inject failing mock/live implementations per feature to test
  errors without destabilizing the whole app.

---

## Functional Requirements (FRs) / User Stories

1. **As a tech reviewer**, I want the prototype to run end-to-end with zero
   backend infrastructure, **so that** I can demo it without setup.
2. **As a tech reviewer**, I want each feature's data layer to expose a clean
   feature-owned repository implementation, **so that** production seams are
   visible and local.
3. **As an engineering lead**, I want Hilt modules to provide mock/live bindings
   per feature, **so that** swapping one feature does not affect unrelated
   features.
4. **As a product manager**, I want mock state to be believable and time-varying,
   **so that** the demo does not feel static.
5. **As a tech reviewer**, I want the same feature use cases and ViewModels to
   work in both modes without modification, **so that** the seam is real.
6. **As a hotel IT operator**, I want production integrations to support the
   right protocol per feature, **so that** the app can integrate with real hotel
   systems.
7. **As a QA engineer**, I want to inject a faulty feature data source, **so
   that** failures can be tested locally.
8. **As an engineering lead**, I want a documented migration checklist per
   feature, **so that** production hardening is incremental.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| Time to swap a feature mock for live implementation | <1 day | migration checklist exercise |
| Code modified outside the owning feature for a data-source swap | 0 lines, excluding app DI/flavor wiring | grep / review |
| Prototype cold-start to first screen | <2 seconds | manual timing |
| Production-mode cold-start target | <3 seconds | profiler / manual timing |
| Crash-free sessions | >99.5% in both modes | Crashlytics / Logcat |
| Feature migration checklist coverage | 100% for implemented features | doc review |
| Test pass rate for mock mode | 100% | CI |

---

## Technical Considerations

- **Interface Design:** Feature repositories expose domain-level operations. For
  example:

  ```kotlin
  interface AccessRepository {
      val lockState: StateFlow<DoorLockState>
      val auditLog: StateFlow<List<AccessAuditEntry>>
      suspend fun unlock(actor: AccessActor): AccessOperationResult
  }
  ```

- **Mock Data Source:** `Mock<Feature>DataSource` simulates delay, state changes,
  and event history with in-memory flows.
- **Live Data Source:** A future live data source handles the real integration,
  credentials, reconnect, and security policies.
- **Hilt Bindings:** Bindings live with the feature data module or app flavor
  source set. Presentation never references concrete data classes.
- **Build Variants:** `mock` and `live` flavors are preferred for demo clarity.
  A `BuildConfig` flag is acceptable only if it does not leak into feature
  presentation or domain.
- **Topic Structure for Gateway-Based Features:** If MQTT is used, topics should
  be feature-specific, e.g. `hotels/{hotelId}/rooms/{roomId}/access/cmd` and
  `hotels/{hotelId}/rooms/{roomId}/access/state`.
- **Logging:** Prototype logs verbose; production logs only actionable events
  with privacy-safe metadata.
- **Feature Flags:** Individual features remain remotely toggleable in
  production, but feature flags must not create feature-to-feature dependencies.
- **Migration Checklist:** Replace mock data source -> wire live integration ->
  integrate identity/reservation authorization -> add telemetry -> harden
  offline/error handling -> security review -> production rollout.
