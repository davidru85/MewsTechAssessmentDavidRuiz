# Design

> **Source of truth for the app's UI design.** This document defines the intended UI; the implementation should follow it, not the other way round.
> It is a living document: a baseline to iterate on, not a finished design system.

**Workflow:** edit this file to describe the design you want, then ask the agent to make the UI match it. The agent treats DESIGN.md as the canonical spec — when code and this document disagree, this document wins and the code is the thing to change. Keep the "Current State" sections describing what *is* built and the "Iteration Backlog" describing what *should* change next; move items up as they're requested.

This prototype deliberately has **no bespoke visual design**. With no PM and no designer (see [NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md)), the design leans on **Material 3 defaults** so that accessibility, dark mode, and a coherent look come "for free." The source of truth for the design is the code, not an external mockup. Design tokens live in code ([docs/design/design-tokens.md](docs/design/design-tokens.md)).

---

## Design Principles

These are the rules the current UI follows and that future work should preserve:

1. **Material 3 first.** Use stock M3 components. Build a custom Composable only when M3 has no equivalent. Today, 100% of the UI is stock M3.
2. **Glanceable, then actionable.** The Dashboard shows whole-room state at a glance; every control acts in one gesture (slide, tap, toggle).
3. **State is honest.** The UI distinguishes *current* vs. *target* (e.g. "Now 23°C · Target 21°C") and surfaces in-flight commands and errors rather than hiding them.
4. **Tokens in code.** Color, typography, shape, and spacing come from the theme — no hard-coded hex or ad-hoc sizes in screens.
5. **Accessible by default.** The baseline in [docs/design/accessibility-baseline.md](docs/design/accessibility-baseline.md) is a constraint, not a nice-to-have.

---

## Theme

Defined in [GuestRoomTheme.kt](core/ui/src/main/java/com/mews/guestroom/core/ui/theme/GuestRoomTheme.kt). A thin wrapper over `MaterialTheme` that swaps color schemes on system dark mode. Typography, shapes, and the remaining color roles are **M3 defaults** (not yet customized).

### Color

The theme overrides only four roles per scheme; everything else is derived by M3.

| Role | Light | Dark | Intent |
|---|---|---|---|
| `primary` | `#006A6A` (teal) | `#54D7D4` (teal) | Brand accent — sliders, selected chips, switches |
| `secondary` | `#6F5B00` (amber) | `#E4C74C` (amber) | Secondary accent |
| `tertiary` | `#8B4A61` (mauve/pink) | `#FFB1C8` (pink) | Tertiary accent |
| `background` | `#FAFDFC` (near-white) | `#101414` (near-black) | App background |

Teal is the de-facto brand color. Dark mode is supported and reached automatically via `isSystemInDarkTheme()`.

**Gaps today:** `surface`, `error`, `onX`, and container roles are all M3 defaults — there is no curated full palette, no brand-aligned error color, and no dynamic-color (Material You) support.

### Typography & Shape

No custom type scale or shapes yet — both are M3 defaults. Screens reference semantic styles (`titleMedium`, `bodyLarge`) rather than raw sizes, so a future type scale can be introduced centrally without touching screens.

---

## Layout & Components

The entire UI today is the **Room Dashboard** ([DashboardScreen.kt](feature/controls/presentation/src/main/kotlin/com/mews/guestroom/feature/controls/presentation/DashboardScreen.kt)).

```
┌─────────────────────────────┐
│  Smart Guest Room      (top) │  TopAppBar
├─────────────────────────────┤
│ [── in-progress bar ──]      │  LinearProgressIndicator (only while a command runs)
│ ┌─────────────────────────┐ │
│ │ Climate                 │ │  Now 23°C · Target 21°C
│ │  ───────●───────────    │ │  Slider, 16–28°C, commits on release
│ ├─────────────────────────┤ │
│ │ Energy scenes           │ │  FilterChip row (one selected)
│ ├─────────────────────────┤ │
│ │ Lights                  │ │  name ───────── [switch] per light
│ ├─────────────────────────┤ │
│ │ Blinds                  │ │  FilterChip row: Open / Half / Closed
│ └─────────────────────────┘ │
└─────────────────────────────┘
   (vertical scroll, 16dp gaps)
```

### Structure

- **`Scaffold`** with a `TopAppBar` ("Smart Guest Room") and a `SnackbarHost` for one-shot messages/errors.
- A single **vertically scrolling `Column`**, 16dp outer padding, 16dp between cards.
- Content is a stack of **`SectionCard`s** — the one reusable building block: an M3 `Card` with a `titleMedium` heading and 16dp inner padding, 12dp internal spacing.

### Card inventory

| Card | Control | Behavior |
|---|---|---|
| **Climate** | `Slider` (16–28°C) | Thumb tracks locally during drag; dispatches a single command on release (debounced). Header shows current vs. target. |
| **Energy scenes** | `FilterChip` row | Single-select; activating a scene orchestrates multiple subsystems at once. |
| **Lights** | `Switch` per light | One row per light: name left, switch right. |
| **Blinds** | `FilterChip` row | Single-select position: Open / Half / Closed. |

All labels are derived by capitalizing enum names (`SLEEP` → "Sleep"), so adding a scene/position needs no UI change.

---

## UI State Model

Defined in [DashboardUiState.kt](feature/controls/presentation/src/main/kotlin/com/mews/guestroom/feature/controls/presentation/DashboardUiState.kt). The screen renders one of two states — **there is no empty state**, because a hotel room always has controls.

| State | Rendered as |
|---|---|
| `Loading` | Centered `CircularProgressIndicator` |
| `Content` | The card stack. `isActionInProgress` adds a top `LinearProgressIndicator` |
| *(errors)* | One-shot `SharedFlow` event → `Snackbar` (not a persistent state) |

State flows reactively: screens collect `StateFlow<UiState>` via `collectAsStateWithLifecycle()`; transient events use `SharedFlow` (replay 0). The UI shape is driven by the pure domain model [RoomControls.kt](feature/controls/domain/src/main/kotlin/com/mews/guestroom/feature/controls/domain/model/RoomControls.kt) (`Climate`, `LightControl`, `BlindPosition`, `EnergyScene`), so live data renders identically to mock.

---

## Accessibility

Baseline in [docs/design/accessibility-baseline.md](docs/design/accessibility-baseline.md):

- 48dp minimum touch target on every interactive element.
- WCAG 2.1 AA contrast for foreground/background pairs.
- TalkBack labels for the primary implemented flow.
- Dynamic font scaling supported.
- Loading / error / on-off states announced clearly.

Stock M3 components satisfy most of this by default. Explicit content-description / state-announcement coverage on the custom-composed rows is a known follow-up (see below).

---

## Current State Summary

**What exists:** A single, dark-mode-aware Room Dashboard, 100% Material 3, with four control cards (climate, scenes, lights, blinds), loading/content/in-progress/error states, a four-role custom color theme on a teal brand accent, and design tokens kept in code.

**What is intentionally not built** (see [PROTOTYPE_OVERVIEW.md](PROTOTYPE_OVERVIEW.md)):

- Other screens (Access/keyless entry, Services & chat) — documented, not designed or coded.
- No navigation beyond a single start destination.
- No custom illustrations, iconography, motion/transition design, or empty/onboarding states.

---

## Iteration Backlog (design)

Ordered roughly by leverage for the next pass:

1. **Curated color palette** — define the full M3 role set (surface, error, containers, `onX`) so the brand reads beyond the four accent roles; consider Material You dynamic color.
2. **Type scale & shapes** — introduce a branded type scale and shape tokens centrally in the theme.
3. **Iconography** — give each card/control a recognizable icon; today they are text-only.
4. **Climate affordance** — evaluate a dial/gauge vs. the current linear slider for a more "thermostat-like" feel.
5. **Accessibility hardening** — explicit `contentDescription` + state announcements on the light/blind/scene controls; verify 48dp targets and AA contrast against the chosen palette.
6. **Multi-screen design language** — once a second feature lands, define navigation (bottom bar?) and a shared card/section pattern across screens.
7. **Motion** — subtle state-change transitions (e.g. temperature drift, scene activation) to reinforce that the room is "alive."
8. **Visual regression coverage** — snapshot tests for light/dark across the Dashboard states.
