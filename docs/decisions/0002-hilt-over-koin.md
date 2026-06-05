# ADR-0002: Hilt Over Koin

## Status
Accepted

## Context
The Android prototype needs dependency injection that is idiomatic, reviewable,
and compatible with Gradle modules and build flavors.

## Decision
Use Hilt. Feature data modules provide repository and data-source bindings.
Feature presentation modules receive use cases through constructor injection.

## Consequences
DI is explicit and Android-standard. Hilt adds annotation processing overhead,
but the clarity is worth it for this assessment.

## Date
2026-06-05

