# Detailed Specification for Working Without a PM or Designer

## Section Overview (Executive Summary)

This section defines the operating model for an engineer-led product effort: how to maintain focus, ship the right scope, and deliver a high-quality visual experience without dedicated PM or designer support. The approach combines disciplined prioritization frameworks (ICE, MoSCoW) with strict adherence to a battle-tested design system (Material Design 3) — replacing bespoke design invention with curated, accessible defaults that let engineering velocity be the bottleneck rather than decision paralysis.

---

## Core Components & Features

- **Prioritization Framework: ICE Matrix** — every candidate feature is scored on Impact (1–10), Confidence (1–10), Ease (1–10); the score is the average. Used to rank and prune.
- **Backup Framework: MoSCoW** — Must / Should / Could / Won't categorization for sprint-level scope. Used to keep MVP tight.
- **Material Design 3 Foundation:** All UI components drawn from the M3 library; no custom design language invented from scratch.
- **Dynamic Color / Material You:** Auto-derive color palette from the user's wallpaper where supported, falling back to a brand-tinted baseline.
- **Dark Mode by Default:** First-class support; both light and dark themes built and tested.
- **Accessibility Baseline:** Minimum 48dp touch targets, WCAG 2.1 AA contrast, screen reader labels, focus order.
- **Custom-Component Discipline:** Custom components are allowed only when M3 doesn't provide an equivalent; each must justify its existence.
- **Decision Log:** A lightweight running document of "why we picked X over Y" to avoid re-litigating closed decisions.
- **Time-Boxing for Polish:** Hard cap on visual polish per feature (e.g., "1 hour on custom theming; ship the default if exceeded") to prevent perfectionism.

---

## Target Stakeholders & Pain Points (Persona Mapping)

### Engineer (acting as PM/designer)
- **Pain:** Tendency to over-design custom components when a default would do.
- **Pain:** Scope creep — every "cool idea" looks high-priority without a framework to rank.
- **Pain:** Visual inconsistency creeping in from one-off decisions.

### Reviewer / Hiring Manager
- **Pain:** Wants to see that the candidate can ship with constraints — demonstrates judgment, not just code skill.
- **Pain:** Looks for evidence of disciplined trade-offs.

### End Guest (indirect)
- **Pain:** Apps with custom-but-inconsistent design feel "off"; well-executed defaults feel professional.

### Future Collaborator (designer who joins later)
- **Pain:** Needs the codebase to be design-system-friendly, not full of one-off custom views that have to be unwound.

---

## Functional Requirements (FRs) / User Stories

1. **As a solo engineer**, I want a clear prioritization framework (ICE / MoSCoW), **so that** I can decide what to build and what to cut without a PM.
2. **As a solo engineer**, I want to ship with Material Design 3 defaults, **so that** I get accessibility, theming, and quality for free.
3. **As a solo engineer**, I want a documented decision log, **so that** I don't relitigate closed choices under fatigue.
4. **As a tech reviewer**, I want to see evidence that low-impact features (e.g., TV control) were deliberately cut, **so that** I can evaluate the candidate's prioritization discipline.
5. **As a guest using the app**, I want consistent visual language across all screens, **so that** the app feels coherent and professional.
6. **As a guest with low vision**, I want large touch targets, high contrast, and screen reader support, **so that** I can use the app independently.
7. **As a guest who prefers dark mode**, I want the app to be visually excellent in dark mode, **so that** I'm not punished for my preference.
8. **As a future designer joining the team**, I want the component library to be M3-based, **so that** my work slots in cleanly.
9. **As a solo engineer**, I want time-boxes on visual polish, **so that** I ship rather than over-iterate.

---

## Success Metrics (KPIs)

| KPI | Target Range | Measurement Method |
|---|---|---|
| % of UI built from M3 components | >85% | Component audit |
| WCAG 2.1 AA contrast compliance | 100% of primary text/background pairs | Accessibility scanner |
| Touch target compliance (≥48dp) | 100% of interactive elements | Layout audit |
| Dark mode parity with light mode | 100% feature coverage | Visual QA matrix |
| Time-box adherence (visual polish) | <2 hours / feature | Self-reported time tracking |
| Decision log entries per sprint | 3–7 | Doc review |
| Features cut via ICE/MoSCoW | ≥30% of originally considered | Scope ledger |
| Accessibility audit pass (TalkBack flow) | 100% of primary flows | Manual test |

---

## Technical Considerations

- **Compose + Material 3:** Use `androidx.compose.material3` with the `MaterialTheme` and `dynamicColor` API for Material You support (Android 12+).
- **Theme Module:** A technical `:core:ui` module may own generic design tokens (color, typography, shape, spacing), but it must not contain feature-specific components or business concepts. Feature-specific UI components stay in the owning feature presentation module.
- **Custom Components Policy:** Custom Composables only when (a) M3 lacks the component, (b) the user need is validated, (c) the custom component is documented and themable.
- **Accessibility Tooling:** Use `Modifier.semantics { }`, content descriptions, and Compose's accessibility testing APIs; run `accessibility-test` rules in CI.
- **Lint Rules:** ktlint + detekt with custom rules to flag hardcoded colors, hardcoded dimensions, and non-M3 component imports.
- **Design Tokens in Code:** Color, typography, and spacing defined as Kotlin objects — never inline magic numbers.
- **Snapshot Testing:** Paparazzi or Roborazzi for visual regression to catch unintended design drift.
- **Decision Log Format:** Lightweight — markdown file in the repo with sections: Context, Options Considered, Decision, Consequences, Date.
- **Brand Override Hooks:** Even if shipping M3 defaults, expose a brand color override so the design system can be retuned without rewriting components.
