# ADR-0005: Compose Navigation Feature Entry Points

## Status
Accepted

## Context
Feature modules must not depend on each other, but the app still needs a single
navigation host.

## Decision
Feature presentation modules expose navigation entry points. `:app` owns the
root graph and composes those entries.

## Consequences
Navigation remains centralized without coupling feature modules together.

## Date
2026-06-05
