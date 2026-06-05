# CONTEXT.md

> A fast orientation snapshot — the minimum an agent or reviewer needs before touching the repo.
> For rules see [AGENTS.md](AGENTS.md); for the full map see [README.md](README.md).

---

## What This Is

A take-home assessment: turn a loosely defined hospitality problem into a validated, prototyped, ready-for-real-solution Android app. Chosen problem: **Smart Guest Room Management** — in-room friction for guests + energy waste for hotels.

Source of truth: [`Product_Builder_Mobile_Android_Take_Home_Task.md`](Product_Builder_Mobile_Android_Take_Home_Task.md).

---

## Current State (honest snapshot)

| Aspect | State |
|---|---|
| Build | ✅ Compiles and runs (`mockDebug`) |
| Modules implemented | `:app`, `:core:common`, `:core:ui` |
| Product features | ⬜ None implemented yet — app shows a placeholder ([AppRoot.kt](app/src/main/java/com/mews/guestroom/AppRoot.kt)) |
| Flavors | `mock` / `live` defined ([app/build.gradle.kts](app/build.gradle.kts)) |
| Next step | First vertical slice: Room Controls / Dashboard |

> Treat anything not listed as "implemented" as **documented but not built**. See [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md).

---

## Architecture in 5 Lines

- Feature-isolated Clean Architecture + MVVM.
- Each feature owns `domain` / `data` / `presentation`.
- `core` is technical only — no shared `:core:domain` / `:core:data`.
- Mock/live swap lives in each feature's `data` module, behind a `domain` contract.
- State via `StateFlow<UiState>` + one-shot `SharedFlow` events.

---

## Tech Stack (essentials)

Kotlin 2.0.21 · JDK 17 · AGP 8.7.3 · Compose (BOM 2024.12.01) + Material 3 · Hilt 2.52 · Navigation Compose 2.8.4 · Coroutines 1.9.0 · minSdk 26 / target 35.

Package: `com.mews.guestroom`. App name: `SmartGuestRoom`.

---

## Build Commands

```bash
./gradlew :app:assembleMockDebug   # build (JDK 17!)
./gradlew :app:installMockDebug    # install
./gradlew test                     # unit tests
```

---

## Key Documents

- Product thinking → [PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md)
- What the app demonstrates → [PROTOTYPE_OVERVIEW.md](PROTOTYPE_OVERVIEW.md)
- Architecture → [ARCHITECTURE.md](ARCHITECTURE.md)
- Delivery / MVP → [DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md)
- Roadmap → [ROADMAP.md](ROADMAP.md)
- Decisions log → [MEMORY.md](MEMORY.md) + [docs/decisions/](docs/decisions/)
- Demo → [DEMO_SCRIPT.md](DEMO_SCRIPT.md)
