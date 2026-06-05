# MEMORY.md

> Durable project memory: decisions, rationale, and gotchas that should survive across sessions and contributors (human or AI).
> This is **project-level** memory committed to the repo — distinct from any agent/tool private memory. For formal records see the ADRs in [docs/decisions/](docs/decisions/); this file is the quick, narrative index plus operational gotchas.

---

## Locked Decisions (do not relitigate without a new ADR)

| Decision | Choice | Rationale | Record |
|---|---|---|---|
| Architecture | Feature-isolated Clean Architecture (no shared `:core:domain`/`:core:data`) | Ownership obvious from path; features evolve independently | [ADR-0001](docs/decisions/0001-feature-isolated-clean-architecture.md) |
| DI | Hilt (over Koin) | Compile-time safety, Android-native | [ADR-0002](docs/decisions/0002-hilt-over-koin.md) |
| State | `StateFlow` for screen state, `SharedFlow` (replay 0) for one-shot events | Lifecycle-safe, explicit | [ADR-0003](docs/decisions/0003-stateflow-vs-sharedflow.md) |
| Mock/live | Boundary per feature in its `data` module, via product flavor | Production seam is local + real | [ADR-0004](docs/decisions/0004-mock-live-boundary-per-feature.md) |
| Navigation | Compose Navigation, feature entry points composed by `:app` | No feature-to-feature navigation | [ADR-0005](docs/decisions/0005-compose-navigation-feature-entry-points.md) |
| `api/impl` | Optional, future-only — never default | Avoid over-engineering the prototype | [ADR-0006](docs/decisions/0006-api-impl-when-needed.md) |
| Design | Material 3 defaults first; bespoke only when justified | UX quality without a designer | [NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md) |

---

## Operational Gotchas

- **JDK 17 required.** Gradle 8.9 fails on the default JDK 25 — use `openjdk@17`. This is the #1 build trap.
- **Use the `mock` flavor** for all local work and demos. `live` has no real data sources yet (by design).
- **Package is `com.mews.guestroom`**; root project name is `SmartGuestRoom`.
- Build artifacts may appear under `*/bin/` and `*/build/` — those are generated; edit sources only.

---

## Current Reality (keep in sync with [CONTEXT.md](CONTEXT.md))

- Implemented: `:app`, `:core:common`, `:core:ui`. No feature module yet; app shows a placeholder.
- Next slice: Room Controls / Dashboard (see [ROADMAP.md](ROADMAP.md)).

---

## Open Questions / To Decide Later

- Which feature is the *second* slice after Controls/Dashboard — Access (keyless entry) or Services? Decide from pilot/validation signals, not preference.
- Whether persistence is ever needed in the prototype (default: no).
- When/if the first `api/impl` split is justified (default: not during the assessment).

---

## How to Update This File

When you make a decision worth remembering: add a row to **Locked Decisions** (and an ADR if architectural), record any new build trap under **Operational Gotchas**, and keep **Current Reality** matching the repo. Remove entries that become wrong.
