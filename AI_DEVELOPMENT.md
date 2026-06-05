# AI-Assisted Development

> How AI tooling accelerated this project and where human judgement stayed in control.
> Deep reference: [spec-08 AI-First Development](specifications/spec-08-ai-first-development.md).

---

## Tools Used / Assumed

- **Claude (Claude Code)** in the IDE/CLI as the primary assistant — architecture discussion, scaffolding, documentation, and review.
- Code-aware in-IDE tools (Cursor/Copilot-class) for inline generation.
- AI is treated as a **force multiplier**, not the source of truth. The engineer owns every diff.

---

## Where AI Accelerated Work

| Activity | How AI helped | Human-owned part |
|---|---|---|
| Project bootstrap | Generated the multi-module Gradle layout, version catalog, Hilt + flavor wiring | Final architecture choice (feature isolation, no shared core domain/data) |
| Architecture boilerplate | Drafted repository contracts, use-case/ViewModel skeletons to a documented template | Dependency rules, module boundaries, naming conventions |
| Mock data | Generated believable room/device fixtures and drift logic | Realism review, edge-case coverage |
| Compose components | Scaffolded Composables from functional descriptions ("thermostat dial with detents") | Visual refinement, accessibility, M3 compliance |
| Documentation | Drafted this docs suite, ADRs, and KDoc from the specs and code | Accuracy vs. real repo state, honest scope statements |
| Code review | Flagged architectural drift, unused imports, concurrency risks | Accept/reject every suggestion |
| Tests | Scaffolded Turbine/ViewModel state tests | Verified assertions test real behaviour, not false confidence |

---

## Examples of AI-Assisted Tasks (in this repo)

- The feature vertical-slice template in [project-structure-blueprint.md](project-structure-blueprint.md) and the ADRs in [docs/decisions/](docs/decisions/) were drafted with AI and reviewed for consistency.
- This documentation suite was generated from the `specifications/` source material and **reconciled against the actual code** (`:app`, `:core:common`, `:core:ui`, `:feature:controls:*`, the `mock`/`live` flavors) so it states honest status, not aspiration.

---

## Human Validation and Corrections

- Every architectural decision was made by the engineer; AI proposed options, the engineer chose ([docs/decisions/](docs/decisions/)).
- Docs were checked against repository reality — e.g. recording that **Controls/Dashboard is implemented** while Access, Services, Notifications, and Profile remain documented future slices.
- AI-generated code passes the same lint / test / review pipeline as hand-written code.

---

## Risks of AI Usage (and mitigations)

| Risk | Mitigation |
|---|---|
| Plausible-but-wrong code / hallucinated APIs | Mandatory human review; compile + test before merge |
| Docs drifting from real code | Reconcile every doc against the repo; mark status honestly |
| False-confidence tests | Review assertions for real behaviour |
| Convention drift across modules | Documented templates + ktlint/detekt |
| Leaking sensitive data | Never feed real guest data / PMS credentials — synthetic data only |

---

## How AI Reduced Cost/Time or Improved Quality

- **>50% time saved** on boilerplate/project setup; **>80%** on mock-data authoring (target estimates).
- More **consistent** structure across modules than hand-repetition would yield.
- Faster documentation and ADR capture, freeing engineering time for architecture and the core flow.

---

## Suggested Live-Demo Talking Points

1. Show an ADR and explain the AI-proposed-options / human-decided workflow.
2. Contrast a mock data source generated with AI against the production seam it implements.
3. Be explicit about where AI was *not* trusted — final architecture, honest scope, test assertions.
4. Frame the headline: AI amplified judgement; it did not replace it.
