# ADR-0004: Mock/Live Boundary Per Feature

## Status
Accepted

## Context
The prototype must run without real hotel hardware but still show a path to
production integrations.

## Decision
Mock and live data sources live inside the owning feature data module. The
feature domain contract remains stable.

## Consequences
Production work is incremental and local. The app may depend on both
presentation and data modules so Hilt can assemble the final graph.

## Date
2026-06-05

