# CONVENTIONS.md

> Coding, structure, and process conventions. Keeps a solo + AI-assisted workflow consistent.
> Rules that are architectural live in [ARCHITECTURE.md](ARCHITECTURE.md) / ADRs; this file is the day-to-day style guide.

---

## Module & Package

- Package root: `com.mews.guestroom`.
- Feature modules: `:feature:<name>:{domain,data,presentation}`, lowercase kebab `<name>` matching the product area (e.g. `controls`, `access`).
- `core` modules are technical only: `:core:common`, `:core:ui`, `:core:testing`. **Never** `:core:domain` / `:core:data`.
- One concept per file; file name matches the primary type.

---

## Naming

| Element | Convention | Example |
|---|---|---|
| Use case | `<Verb><Noun>UseCase` | `ToggleLightUseCase`, `ActivateSleepModeUseCase` |
| Repository contract (domain) | `<Feature>Repository` | `ControlsRepository` |
| Repository impl (data) | `<Feature>RepositoryImpl` | `ControlsRepositoryImpl` |
| Data sources | `Mock<Feature>DataSource` / `Live<Feature>DataSource` | `MockControlsDataSource` |
| Compose screen | `<Feature>Screen` | `DashboardScreen` |
| ViewModel | `<Feature>ViewModel` | `DashboardViewModel` |
| UI state | `<Feature>UiState` | `DashboardUiState` |
| Nav entry point | `NavGraphBuilder.<feature>Graph()` | `controlsGraph()` |

---

## Kotlin Style

- Idiomatic Kotlin; immutability by default (`val`, immutable data classes, `List` not `MutableList` in public APIs).
- `:feature:*:domain` is pure Kotlin — no Android/Compose/Hilt imports.
- Inject dispatchers (from `:core:common`) — never hardcode `Dispatchers.IO` in business logic.
- Errors flow as `DataResult<T>` (in `:core:common`), not thrown exceptions across layers.
- No magic numbers/strings in UI — use design tokens (`:core:ui`) and string resources.
- Format/lint with ktlint conventions; keep functions small and named by intent.

---

## State & Concurrency

- Screen state: `StateFlow<UiState>`, collected with `collectAsStateWithLifecycle()`.
- One-shot events (snackbar, navigation): `SharedFlow` with `replay = 0`.
- ViewModels expose immutable flows + public intent functions (`onXxx()`); no public mutable state.
- Structured concurrency in `viewModelScope`.

---

## Compose / UI

- Material 3 components first; bespoke Composables only when M3 lacks an equivalent and the need is validated.
- Build and test both light and dark themes.
- Accessibility baseline is mandatory ([docs/design/accessibility-baseline.md](docs/design/accessibility-baseline.md)): ≥48dp targets, AA contrast, TalkBack labels.
- Feature-specific components live in the feature `presentation` module, not `:core:ui`.

---

## Git & PRs

- One branch per unit of work: `feature/<name>`, `fix/<scope>`, `docs/<scope>`, `chore/<scope>`.
- Conventional commits: `feat:`, `fix:`, `docs:`, `chore:`, `test:`.
- Every feature merges via a PR; squash-merge to keep `main` linear and always demoable.
- Full process: [docs/development/branching-and-pull-requests.md](docs/development/branching-and-pull-requests.md).

---

## Documentation

- Docs must match real repo state — mark unbuilt work as planned, never as done.
- Architectural decisions get an ADR ([docs/decisions/adr-template.md](docs/decisions/adr-template.md)); narrative index in [MEMORY.md](MEMORY.md).
- Reference files with relative Markdown links.
- AI-assisted files pass the same review/lint/test gate as hand-written code.
