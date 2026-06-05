# ADR-0003: StateFlow vs. SharedFlow

## Status
Accepted

## Context
Screens need stable state and occasional one-time events such as errors or
navigation requests.

## Decision
Use `StateFlow` for durable UI state and `SharedFlow` for one-shot events.

## Consequences
State is replayable and lifecycle-safe, while transient events do not become
sticky after configuration changes.

## Date
2026-06-05

