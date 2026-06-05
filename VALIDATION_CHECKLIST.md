# VALIDATION_CHECKLIST.md

> An actionable pre-submission / pre-demo gate. Distinct from [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md) (which *maps* requirements to evidence) — this is the runnable checklist you tick off before sharing the repo or presenting.

---

## 1. Build & Run

- [ ] Clean checkout builds: `./gradlew :app:assembleMockDebug` (JDK 17).
- [ ] App installs and launches on an emulator/device: `./gradlew :app:installMockDebug`.
- [ ] No crashes/ANRs during the scripted flow.

## 2. Architecture Integrity

- [ ] No `:core:domain` or `:core:data` module exists.
- [ ] `:feature:*:domain` has zero Android/Compose/Hilt imports.
- [ ] No feature module depends on another feature module.
- [ ] `presentation` does not depend on `data`.
- [ ] Mock/live difference is confined to each feature's `data` module.

## 3. Prototype / Demo

- [ ] Core flow (Dashboard: thermostat, lights/blinds, energy mode) works on the `mock` flavor.
- [ ] Mock state is believable and time-varying (e.g. temperature drift), not static.
- [ ] Demo-critical states present: loading, content, error (transient message).
- [ ] [DEMO_SCRIPT.md](DEMO_SCRIPT.md) rehearsed end-to-end within ~60–90s.

## 4. Tests

- [ ] **TDD Protocol followed** — new behaviour written test-first (red → green → refactor), each phase passing the owner's manual-review gate before commit / push / PR; compulsory per [TESTING_STRATEGY.md](TESTING_STRATEGY.md#the-tdd-protocol-mandatory).
- [ ] Unit tests pass: `./gradlew test`.
- [ ] First slice covered: domain use-case, ViewModel state (Turbine), one Compose smoke test ([TESTING_STRATEGY.md](TESTING_STRATEGY.md)).
- [ ] Static checks pass (ktlint/detekt; architecture rules).

## 5. Documentation Honesty

- [ ] Every doc matches real repo state — no claimed-but-unbuilt features.
- [ ] [ASSESSMENT_ALIGNMENT.md](ASSESSMENT_ALIGNMENT.md), [CONTEXT.md](CONTEXT.md), [ROADMAP.md](ROADMAP.md), [MEMORY.md](MEMORY.md) reflect current status.
- [ ] Every assessment bullet maps to at least one document.
- [ ] Relative Markdown links resolve.

## 6. Assessment Requirement Coverage

- [ ] Hospitality problem identified + who/why ([PRODUCT_DISCOVERY.md](PRODUCT_DISCOVERY.md)).
- [ ] Discovery & validation explained (signals + experiments).
- [ ] Kotlin Android prototype with mock data, no backend.
- [ ] Core user flow demonstrable on device/emulator.
- [ ] AI-assisted development explained ([AI_DEVELOPMENT.md](AI_DEVELOPMENT.md)).
- [ ] MVP & delivery strategy explained ([DELIVERY_STRATEGY.md](DELIVERY_STRATEGY.md)).
- [ ] Adoption strategy & metrics explained ([ADOPTION_METRICS.md](ADOPTION_METRICS.md)).
- [ ] Working without PM/designer explained ([NO_PM_NO_DESIGNER.md](NO_PM_NO_DESIGNER.md)).
- [ ] Code shared + presentation/demo material ready.

## 7. Submission Hygiene

- [ ] `main` is green and demoable; work merged via PRs.
- [ ] No secrets, real guest data, or PMS credentials committed.
- [ ] `.gitignore` excludes build artifacts (`build/`, `bin/`, `.gradle/`).
- [ ] README run/test instructions are accurate.
- [ ] Presentation prepared and (per the task) sent one day prior to the session.

---

### Sign-off

- [ ] Discovery validated (or go/no-go signals documented).
- [ ] Prototype demoable.
- [ ] Docs honest and complete.
- [ ] Ready to share repo + present.
