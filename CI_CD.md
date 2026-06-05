# CI/CD Strategy

> How continuous integration keeps `main` buildable, and the path to continuous delivery if the prototype moves toward production.
> Scope note: the assessment treats production CI/CD as a **non-goal** unless requested. This document defines a *lightweight, prototype-appropriate* CI that runs today, and frames full CD as future evolution — consistent with [DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md) and [TODO.md](TODO.md).

---

## Goal

Keep `main` **always green and demoable**. Every pull request (and every push to `main`) must build the mock flavor and pass unit tests before merge. This mirrors the per-feature PR workflow in [docs/development/branching-and-pull-requests.md](docs/development/branching-and-pull-requests.md) and the Definition of Done in [AGENTS.md](AGENTS.md).

---

## What Runs Today (CI)

Implemented in [.github/workflows/ci.yml](.github/workflows/ci.yml):

| Step | Command | Why |
|---|---|---|
| Set up JDK 17 | Temurin 17 | Gradle 8.9 fails on newer JDKs (see [MEMORY.md](MEMORY.md)) |
| Build | `./gradlew :app:assembleMockDebug` | Proves the app compiles from a clean checkout |
| Unit tests | `./gradlew test` | Domain/use-case, ViewModel, and data tests ([TESTING_STRATEGY.md](TESTING_STRATEGY.md)) |
| Test reports | upload-artifact | Surfaces failures for review |

**Triggers:** `pull_request` → `main` and `push` → `main`. Superseded runs on the same ref are cancelled to save CI minutes.

---

## Gates

- A PR is mergeable only when CI is green **and** the [Definition of Done](AGENTS.md) is met.
- Recommended (configure in GitHub repo settings): mark **Build & Test (mock, JDK 17)** as a **required status check** on `main`, and require PRs before merging. This enforces the "nothing committed directly to `main`" rule from the branching strategy.

---

## Deliberately NOT in CI Yet

Kept out to avoid false-red builds and over-engineering the prototype:

- **Static analysis (ktlint/detekt)** + architecture-rule checks (no Android imports in `:feature:*:domain`, no cross-feature deps) — these tools are not yet configured in the version catalog. Tracked in [TODO.md](TODO.md); add the step the same day the tooling lands.
- **Instrumented/Compose UI tests** — require an emulator on CI; add once the first feature slice exists.
- **Coverage gates** — explicitly out of scope for the assessment ([TESTING_STRATEGY.md](TESTING_STRATEGY.md)).

---

## Future Production CD Path

When the product moves beyond the prototype (Phase 2+ in [ROADMAP.md](ROADMAP.md)), CI extends into continuous delivery:

1. **Quality gates** — enable ktlint/detekt, architecture checks, and the Compose smoke test as required checks.
2. **Signed builds** — `assembleLiveRelease` with secrets-managed signing config (keystore via GitHub Secrets, never committed).
3. **Distribution** — upload to an internal track (Play Console internal testing / Firebase App Distribution) for pilot rooms.
4. **Versioning** — automate `versionCode`/`versionName` from tags or run number.
5. **Staged rollout** — internal → closed pilot → production, gated on pilot metrics ([ADOPTION_METRICS.md](ADOPTION_METRICS.md)).
6. **Observability hooks** — wire crash reporting / mapping-file upload into the release job.

**Rule:** CD additions follow the same feature-isolation discipline — pipeline changes never require introducing `:core:domain` or `:core:data`, and live-flavor work stays inside the owning feature's `data` module.

---

## Local Equivalent

Run the same gates locally before opening a PR:

```bash
./gradlew :app:assembleMockDebug   # JDK 17
./gradlew test
```
