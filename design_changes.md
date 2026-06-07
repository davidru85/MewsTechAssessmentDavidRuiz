# Design Changes — for owner review

> Running log of UI changes, recorded for review. Each entry notes what changed visually/behaviourally and how it relates to [DESIGN.md](DESIGN.md) (the canonical UI spec).

---

## Phase 1.5 · Slice A — Wire Climate Mode buttons (`feat/controls-climate-mode`)

**Scope:** behaviour-only. No layout, color, typography, or spacing changes — the Climate section renders exactly as before.

- **Climate mode buttons are now interactive.** "Auto Fan" and "Cooling" ([DashboardScreen.kt](feature/controls/presentation/src/main/kotlin/com/mews/guestroom/feature/controls/presentation/DashboardScreen.kt) `ClimateModeButtons`) previously had empty `onClick` handlers (display-only). Tapping now dispatches a command: **Auto Fan → `AUTO`**, **Cooling → `COOL`**. The active button keeps the existing high-fill style; the inactive one stays outlined.
- **Tapping the already-active mode is inert.** No command is sent, the active scene is not cleared, and no busy spinner flashes (guarded in `DashboardViewModel.onSetClimateMode`).
- **Adjusting the temperature slider no longer resets the mode.** Previously moving the slider forced the mode back to `AUTO`; it now preserves the user's chosen mode.

**DESIGN.md alignment:** matches §2 (two mode buttons, high-fill when active). No spec change required for this slice.

**Not in DESIGN.md yet (future slices, will be specced before implementing):** faulty-device warning indicator (Slice B), "Coming soon" placeholder screens (Slice C).
