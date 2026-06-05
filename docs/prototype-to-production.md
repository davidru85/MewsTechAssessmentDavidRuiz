# Prototype To Production Runbook

The prototype should start with one selected vertical slice using an in-memory
mock data source. Production hardening happens feature by feature.

## Feature Migration

1. Keep `:feature:<name>:domain` unchanged.
2. Keep `:feature:<name>:presentation` unchanged unless production introduces a
   genuinely new user-facing state.
3. Replace or complete the live data source in `:feature:<name>:data`.
4. Bind the live data source for the `live` flavor.
5. Add real credential handling and reservation-window authorization.
6. Add privacy-safe telemetry and audit-log export.
7. Test lock failure, timeout, offline, and staff-entry scenarios.
8. Run security review for BLE / MQTT / gateway communication.

## Rule

No production migration should require creating `:core:domain` or `:core:data`.
