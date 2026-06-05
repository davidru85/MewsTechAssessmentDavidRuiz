# Branching & Pull Request Strategy

> Process source of truth for how work moves from a feature branch into `main`.
> This mirrors the project's feature-isolated architecture: just as each product
> feature owns its `domain`, `data`, and `presentation` modules, each feature is
> developed on its own branch and merged through a pull request.

---

## 1. Core Rule

**Every feature is developed on a dedicated branch and merged into `main` via a
pull request.** Nothing is committed directly to `main`.

This keeps `main` always buildable and demoable, gives each vertical slice a
reviewable unit of work, and matches the one-feature-at-a-time delivery bias in
[spec-05 Delivery Strategy](../../specifications/spec-05-delivery-strategy.md).

---

## 2. Branch Naming

One branch per unit of work, named `<type>/<scope>`:

| Type | When to use | Example |
|---|---|---|
| `feature/<name>` | A product feature vertical slice. `<name>` matches the feature module (`:feature:<name>:*`). | `feature/controls` |
| `chore/<scope>` | Build, tooling, dependency, or config work. | `chore/version-catalog` |
| `docs/<scope>` | Documentation-only changes (specs, ADRs, this process). | `docs/git-workflow` |
| `fix/<scope>` | A bug fix on existing behavior. | `fix/thermostat-drift` |

`<name>` / `<scope>` is short, lowercase, and kebab-cased. A feature branch
covers the whole slice (domain → data → presentation → app wiring → tests), not
one branch per layer.

---

## 3. Lifecycle

```text
main ──●──────────────────────────●──  (always green, always demoable)
        \                         /
         ●──●──●  feature/controls   (branch → commit → PR → squash merge)
```

1. **Branch** off the latest `main`:
   `git switch main && git pull && git switch -c feature/controls`
2. **Commit** in small, conventional-commit increments
   (`feat:`, `fix:`, `docs:`, `chore:`, `test:`).
3. **Open a PR** into `main` once the slice builds and tests pass.
4. **Review** against the checklist below.
5. **Squash merge** to keep a linear, readable history on `main`.
6. **Delete** the feature branch after merge.

---

## 4. Definition of Done (PR merge gate)

A feature PR is mergeable only when:

- [ ] The slice is complete end-to-end (domain, data incl. mock data source,
      presentation, and `:app` wiring).
- [ ] **Behaviour was developed test-first** under the mandatory
      [TDD Protocol](../../TESTING_STRATEGY.md#the-tdd-protocol-mandatory)
      (red → green → refactor, each phase gated by the owner's manual review
      before commit / push / PR). The PR itself is opened only at Refactor
      sub-phase 3, after review.
- [ ] `./gradlew :app:assembleMockDebug` succeeds (JDK 17 — see project memory).
- [ ] Unit tests pass for the feature's domain, data, and presentation layers
      (per [blueprint §8](../../project-structure-blueprint.md)).
- [ ] No feature module depends on another feature module, and
      `:feature:*:domain` has no Android imports.
- [ ] Docs updated where relevant (feature status, ADRs).

---

## 5. PR Description Template

```text
## What
One-line summary of the feature slice.

## Why
Link to the spec / problem this advances.

## How to verify
Steps to run the mock flavor and exercise the core flow.

## Architecture notes
Confirm feature isolation held (no cross-feature deps, domain stays pure).
```

---

## 6. Working Solo (No PM / Designer)

Even without a second reviewer, PRs are still opened per feature. The PR becomes
the self-review and demo record for the slice — consistent with the solo
operating model in
[spec-07](../../specifications/spec-07-no-pm-designer.md). Self-review against
the Definition of Done before merging.
