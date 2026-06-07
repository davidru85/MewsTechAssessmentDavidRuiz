# Design Changes — for owner review

> Running log of UI changes, recorded for review. Each entry notes what changed visually/behaviourally and how it relates to [DESIGN.md](DESIGN.md) (the canonical UI spec).

---

## Phase 1.5 · Slice A — Wire Climate Mode buttons (`feat/controls-climate-mode`)

**Scope:** behaviour-only. No layout, color, typography, or spacing changes — the Climate section renders exactly as before.

- **Climate mode buttons are now interactive.** "Auto Fan" and "Cooling" ([DashboardScreen.kt](feature/controls/presentation/src/main/kotlin/com/mews/guestroom/feature/controls/presentation/DashboardScreen.kt) `ClimateModeButtons`) previously had empty `onClick` handlers (display-only). Tapping now dispatches a command: **Auto Fan → `AUTO`**, **Cooling → `COOL`**. The active button keeps the existing high-fill style; the inactive one stays outlined.
- **Tapping the already-active mode is inert.** No command is sent, the active scene is not cleared, and no busy spinner flashes (guarded in `DashboardViewModel.onSetClimateMode`).
- **Adjusting the temperature slider no longer resets the mode.** Previously moving the slider forced the mode back to `AUTO`; it now preserves the user's chosen mode.

**DESIGN.md alignment:** matches §2 (two mode buttons, high-fill when active). No spec change required for this slice.

**Not in DESIGN.md yet (future slices, will be specced before implementing):** "Coming soon" placeholder screens (Slice C).

---

## Phase 1.5 · Slice B — Faulty device indicator (`feat/controls-faulty-device-indicator`)

**Scope:** Lighting section — surface a deliberately-faulty device up-front instead of only on a failed toggle.

**DESIGN.md spec change (§4 Lighting):** added a "Faulty device state" rule — a row whose device is unreachable shows a small **warning icon (error tint)** immediately left of the switch, and its **switch is disabled** (non-interactive). No caption text. Also corrected §4 item names to match the implementation (Ceiling, Bedside, Bathroom).

**Behaviour:** activating an Atmosphere scene no longer turns a faulty light on — scenes skip unreachable devices, so the Bathroom row never shows a warning + an "on" switch at the same time.

**UI change (`DashboardScreen.kt` `LightingSection`):**
- A ⚠ warning icon (`Icons.*` warning, `colorScheme.error`) renders just left of the switch for a faulty light.
- The faulty row's `Switch` is disabled (`enabled = !light.isFaulty`).
- Mock seeds the **Bathroom** light as faulty.

**Owner-approved decisions (this session):** switch disabled (not kept interactive); icon-only (no "Unreachable"/"Offline" caption).

**Trade-off:** disabling the switch removes the error snackbar from that row's tap. The data/VM error path stays covered by existing tests (`toggleLight_faultyDevice…`, `onToggleLight_whenRepositoryFails…`).

---

## Phase 1.5 · Slice C — Placeholder routing for Services & Keys (`feat/placeholder-routing`)

**Scope:** navigation shell + two placeholder screens. Build-alongside (navigation/Compose), no unit tests.

**DESIGN.md spec change:** §6 gains a "persistence" note (app-level shared bar); new **§7 Placeholder Screens** — centered tab icon + title + "Coming soon" caption.

**Architecture (owner-approved: app-level shell):**
- `AppRoot` is now an app-level `Scaffold` owning the persistent `NavigationBar`, wrapping the `NavHost`. The active tab follows the current route via `currentBackStackEntryAsState`. Tabs use single-top + save/restore state.
- `DashboardScreen`'s `Scaffold` **no longer renders the bottom bar** (it keeps its top bar, snackbar, and content); the bar is now app-level. `RoomBottomNav` and its now-unused imports were removed from the feature.
- New routes `SERVICES_ROUTE` / `KEYS_ROUTE` in `:app`; both render a shared `PlaceholderScreen` (new, in `:core:ui` `components`).

**Decisions (this session):** app-level shell (vs per-screen callbacks); placeholder = icon + title + "Coming soon".
