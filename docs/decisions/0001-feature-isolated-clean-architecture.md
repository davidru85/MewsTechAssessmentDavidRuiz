# ADR-0001: Feature-Isolated Clean Architecture

## Status
Accepted

## Context
The project needs to demonstrate modularity strong enough that a feature can be
implemented, tested, replaced, or removed without centralizing business logic in
shared modules.

## Decision
Each product feature owns `domain`, `data`, and `presentation` modules. Shared
`core` modules are technical only. The project will not create `:core:domain`
or `:core:data`.

The `api/impl` pattern will be applied if necessary. It is not part of the
default starting shape, but it should be introduced for a feature when that
feature needs a stable public contract, implementation hiding, team-level
ownership boundaries, or compile-time isolation beyond the base
`domain` / `data` / `presentation` split.

## Consequences
Feature ownership is clear and cross-feature coupling is minimized. Some domain
types may be duplicated or later extracted only if a real shared platform
concept emerges. If `api/impl` is introduced, it must remain feature-scoped and
must not reintroduce shared `:core:domain` or `:core:data` modules.

## Date
2026-06-05
