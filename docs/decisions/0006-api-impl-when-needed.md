# ADR-0006: api/impl When Needed

## Status
Accepted

## Context
The project requires feature isolation, but not every feature needs an
additional public/private module split from day one. Applying `api/impl` too
early would add ceremony before there is a stable public contract to protect.

## Decision
Apply the `api/impl` pattern if necessary. A feature may introduce
`:feature:<name>:api` and `:feature:<name>:impl` when it needs to expose a
stable public contract while hiding implementation details.

The base feature shape remains:

```text
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
```

The evolved shape, when justified, is:

```text
:feature:<name>:api
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
:feature:<name>:impl
```

## Consequences
The project avoids premature module multiplication while keeping a clear path to
stronger encapsulation. The `api/impl` split must remain feature-scoped and must
not create shared `:core:domain` or `:core:data` modules.

## Date
2026-06-05
