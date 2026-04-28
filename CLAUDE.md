# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project context

ConsultMe is a **Jetpack Compose template** for new Android apps — multi-module, Kotlin-only, with code-quality plumbing (Spotless, Detekt, Lint) wired in. Apps generated from this template start by replacing the placeholder content in `:feature-example` (see README.md "How to Rename and Refactor"). Default package is `com.thecompany.consultme` and is expected to be renamed downstream.

**Roadmap and ongoing improvements** live in `docs/IMPROVEMENT_PLAN.md`. Check it before starting non-trivial work — it lists what's intentionally deferred (AGP 9, Hilt 2.59+, Kotlin 2.3.20) and what the next planned phases are.

## Common commands

CI runs these in order; locally you typically want the same gates before opening a PR:

```bash
./gradlew spotlessCheck     # formatting (fails on missing license header / ktlint violations)
./gradlew spotlessApply     # autofix
./gradlew detekt            # static analysis (config: config/detekt.yml)
./gradlew lintRelease       # Android lint, release variant
./gradlew test              # all unit tests
./gradlew connectedAndroidTest  # instrumented tests (needs device/emulator)
```

Running one test:
```bash
./gradlew :feature-example:testDebugUnitTest --tests "com.thecompany.consultme.feature.example.ui.ExampleUnitTest"
./gradlew :feature-example:testDebugUnitTest --tests "*ExampleUnitTest.someMethod"
```

`spotlessApply` is the autofix; the license header gets injected with the current year and the literal `MyCompany` string. Adopters of the template should rename `MyCompany` in the root `build.gradle.kts` Spotless config (planned to be parameterized in Phase 2 — see `docs/IMPROVEMENT_PLAN.md`).

Lint baselines (`<module>/lint-baseline.xml`) exist per module — regenerate with `./gradlew :<module>:updateLintBaseline` rather than hand-editing.

## Module graph

- `:app` — Application module. Wires Hilt (`ConsultMeApplication`), Compose root, navigation. Depends on `:core-ui`, `:feature-example`.
- `:feature-*` — Screen-level features (currently only `:feature-example`, a placeholder for adopters to replace; rename it once you know what you're building).
- `:core-ui` — Shared Compose UI building blocks.
- `:core-data` → `:core-database` — Repository and persistence layers.
- `:core-testing` — **Test fixtures shared across every module.** Uses `api(...)` (not `implementation`) to re-export JUnit, Truth, Turbine, MockK, Hilt testing, coroutines-test, Espresso. It also provides `HiltTestRunner`, which every module references as its `testInstrumentationRunner`. Consume it via `testImplementation(project(":core-testing"))` and `androidTestImplementation(project(":core-testing"))` — do **not** add JUnit/Espresso/etc. directly in module build scripts.

## Conventions enforced by tooling

- **License header**: Spotless (configured in root `build.gradle.kts`) requires `// Copyright $YEAR MyCompany` on every `.kt` and `.gradle.kts` file. Placement is delimiter-driven: above the `package` line for Kotlin, above the first `/*` for Gradle Kotlin scripts. New files without the header fail `spotlessCheck`. The "MyCompany" / `$YEAR` literals get rewritten by `spotlessApply`.
- **Toolchain**: Every module sets `jvmToolchain(17)`, JVM target 17, and `freeCompilerArgs = ["-Xcontext-receivers"]`. Match this when adding modules.
- **DI**: Hilt + KSP. `kapt` is intentionally left commented out across build scripts — use `ksp(libs.hilt.compiler)`. Modules that need DI must apply both `libs.plugins.hilt.gradle` and `libs.plugins.ksp` (see `:core-data` for the canonical pattern).
- **Compose**: BOM-managed (`libs.androidx.compose.bom`). Library modules disable `buildFeatures.compose` unless they actually emit composables. `buildConfig`, `aidl`, `renderScript`, `shaders` are turned off everywhere.
- **SDKs**: `compileSdk = 36`, `targetSdk = 36`, `minSdk = 25`.

## Dependency management

All versions live in `gradle/libs.versions.toml`. Dependabot is enabled with grouped updates (`androidx`, `kotlin-and-coroutines`, `gradle-and-plugins`, `jetpack-compose`, `testing-libs`).

Active ignore rules in `.github/dependabot.yml` — leave these alone unless doing the corresponding migration:

- `com.android.application` / `com.android.library` major versions blocked. AGP 9.x removes the `kotlin-android` plugin requirement and forces Gradle ≥ 9.4 — handle as a dedicated migration PR, not a bot bump.
- `com.google.dagger.hilt.android` `>=2.59` blocked. Hilt 2.59 hard-requires AGP 9.
- `org.jetbrains.kotlin.android` and `org.jetbrains.kotlin.plugin.compose` `>=2.3.20` blocked. Kotlin 2.3.20+ promotes `-Xcontext-receivers` from a warning to a hard error in release variants — unblocking is gated on the `-Xcontext-receivers` → `-Xcontext-parameters` migration tracked in `docs/IMPROVEMENT_PLAN.md` "Known follow-ups."

## Recommended Claude Code skills

Google's official Android Claude Code skills live at <https://github.com/android/skills>. Skills relevant to this template:

- `agp-9-upgrade` — playbook for the Phase 5 AGP 8 → 9 migration.
- `r8-analyzer` — helps analyze keep rules when ramping Phase 4 (release minification).
- `edge-to-edge` — adoption guide if/when the template adopts edge-to-edge.

Skills are not vendored — install locally when starting the matching phase. See `docs/IMPROVEMENT_PLAN.md` for the phase mapping.

## CI / branch protection

`main` is protected:
- Direct pushes rejected — every change goes through a PR.
- `build_and_test` (the workflow in `.github/workflows/android_ci.yml`) is a required check.
- GitHub auto-merge is **disabled** at the repo level. Merging requires `gh pr merge --admin --squash --delete-branch` (admin override) or clicking merge in the UI.
- Dependabot opens grouped PRs weekly; they get squash-merged like any other PR.
