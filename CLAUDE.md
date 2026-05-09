# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project context

ConsultMe is a **Jetpack Compose template** for new Android apps — multi-module, Kotlin-only, with code-quality plumbing (Spotless + ktlint, Android Lint) wired in. Apps generated from this template start by replacing the placeholder content in `:feature-example` (see README.md "How to Rename and Refactor"). Default package is `com.thecompany.consultme` and is expected to be renamed downstream.

**Roadmap and ongoing improvements** live in `docs/IMPROVEMENT_PLAN.md`. Check it before starting non-trivial work — it lists what's intentionally deferred (AGP 9, Hilt 2.59+, Kotlin 2.3.20) and what the next planned phases are.

## Common commands

CI runs these in order; locally you typically want the same gates before opening a PR:

```bash
./gradlew spotlessCheck     # formatting (fails on missing license header / ktlint violations)
./gradlew spotlessApply     # autofix
./gradlew lintRelease       # Android lint, release variant
./gradlew test              # all unit tests
./gradlew koverHtmlReport   # aggregated coverage at build/reports/kover/html/
./gradlew moduleGraph       # regenerate docs/MODULE_GRAPH.md (CI fails if stale)
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

- `:app` — Application module. Wires Hilt (`ConsultMeApplication`), Compose root, navigation. Depends on `:core-designsystem`, `:core-ui`, `:feature-example`.
- `:feature-*` — Screen-level features (currently only `:feature-example`, a placeholder for adopters to replace; rename it once you know what you're building). Get `:core-designsystem` + `:core-ui` + `:core-testing` automatically via the `consultme.android.feature` convention.
- `:core-designsystem` — Compose theme + design tokens (colors, typography). Owns `ConsultMeTheme`. Adopters extend with icon registries, custom components, etc.
- `:core-ui` — Shared Compose composables not part of the design system (loading/empty/error states). Currently a scaffold.
- `:core-model` — Pure-Kotlin data classes (no Android, no Hilt). Built with `consultme.jvm.library`.
- `:core-common` — Pure-Kotlin shared utilities; ships the NIA-style `Dispatcher` qualifier + `AppDispatchers` enum. Built with `consultme.jvm.library`.
- `:core-domain` — Pure-Kotlin use-cases. Depends on `:core-model`. Built with `consultme.jvm.library`.
- `:core-data` → `:core-database` — Repository and persistence layers.
- `:core-testing` — **Test fixtures shared across every module.** Uses `api(...)` (not `implementation`) to re-export JUnit, Truth, Turbine, MockK, Hilt testing, coroutines-test, Espresso. It also provides `HiltTestRunner`, which every module references as its `testInstrumentationRunner`. Consume it via `testImplementation(project(":core-testing"))` and `androidTestImplementation(project(":core-testing"))` — do **not** add JUnit/Espresso/etc. directly in module build scripts.

## Conventions enforced by tooling

- **License header**: Spotless (configured in root `build.gradle.kts`) requires `// Copyright $YEAR MyCompany` on every `.kt` and `.gradle.kts` file. Placement is delimiter-driven: above the `package` line for Kotlin, above the first `/*` for Gradle Kotlin scripts. New files without the header fail `spotlessCheck`. The "MyCompany" / `$YEAR` literals get rewritten by `spotlessApply`.
- **Convention plugins**: Module build scripts compose plugins from `build-logic/` instead of redeclaring AGP/Kotlin/JVM/lint config. Available: `consultme.android.application`, `consultme.android.library`, `consultme.android.compose`, `consultme.android.hilt`, `consultme.android.feature` (library + compose + hilt + standard feature deps + `:core-testing`), `consultme.android.room` (KSP + Room runtime/ktx/compiler + schema export), `consultme.android.test` (`com.android.test`, for benchmark modules), `consultme.android.lint` (custom-Lint-check modules), `consultme.jvm.library` (pure Kotlin, no AGP), `consultme.kover` (coverage; auto-applied by every Android/JVM convention), `consultme.modulegraph` (root-only; registers the `:moduleGraph` task that emits `docs/MODULE_GRAPH.md` via a Strategy-pattern renderer). Shared helpers live in `build-logic/convention/src/main/kotlin/com/thecompany/consultme/buildlogic/AndroidExtensions.kt` — extend those rather than duplicating config in module scripts.
- **Toolchain**: `jvmToolchain(17)`, JVM target 17, `freeCompilerArgs = ["-Xcontext-parameters"]` — set by the convention plugins.
- **DI**: Hilt + KSP, applied via `consultme.android.hilt`. The convention adds `hilt-android` impl + `hilt-compiler` ksp; don't redeclare them per module.
- **Compose**: BOM-managed via `consultme.android.compose` (Compose BOM + ui/graphics/tooling-preview/material3). The convention enables `buildFeatures.compose`. `buildConfig`, `aidl`, `renderScript`, `shaders` are turned off everywhere by the library/application conventions.
- **SDKs**: `compileSdk = 36`, `targetSdk = 36`, `minSdk = 26` (set by the conventions).

## Dependency management

All versions live in `gradle/libs.versions.toml`. Dependabot is enabled with grouped updates (`androidx`, `kotlin-and-coroutines`, `gradle-and-plugins`, `jetpack-compose`, `testing-libs`).

Active ignore rules in `.github/dependabot.yml` — leave these alone unless doing the corresponding migration:

- `com.android.application` / `com.android.library` major versions blocked. AGP 9.x removes the `kotlin-android` plugin requirement and forces Gradle ≥ 9.4 — handle as a dedicated migration PR, not a bot bump.
- `com.google.dagger.hilt.android` `>=2.59` blocked. Hilt 2.59 hard-requires AGP 9.

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
- `instrumented_tests` (Gradle Managed Device job) is **not yet required** — let it bake for a few PRs, then promote it via Repo → Settings → Branches → main.
- GitHub auto-merge is **disabled** at the repo level. Merging requires `gh pr merge --admin --squash --delete-branch` (admin override) or clicking merge in the UI.
- Dependabot opens grouped PRs weekly (Gradle deps + workflow action SHAs); they get squash-merged like any other PR.
- **Fork PR approval**: Repo → Settings → Actions → General → "Require approval for first-time contributors" should stay enabled. Without it, anyone forking can spam workflow runs through unlimited public-repo Action minutes. (No $ cost on a public repo, but GitHub may rate-limit the org under abuse.)
- Third-party actions in workflows are **pinned to commit SHAs** (with the version tag in a trailing comment). Dependabot's `github-actions` ecosystem auto-bumps them weekly. When reviewing those PRs, glance at the upstream changelog before merging — that's the value SHA-pinning gives you.

## Versioning and tags

Tags follow SemVer with a template-adopter lens: a "breaking change" is one that affects a downstream fork, not just an internal refactor.

| Bump | When |
|------|------|
| **MAJOR** (`vX.0.0`) | Breaking changes for adopters — `minSdk` bump, AGP/Kotlin/Hilt major migration, convention-plugin API renames, removing or renaming a module |
| **MINOR** (`v1.X.0`) | A phase landing, new convention plugin, new feature module template, additive opt-in tooling |
| **PATCH** (`v1.0.X`) | Bug fixes, doc-only changes, dep bumps from Dependabot |

**Conventions:**
- **Tag at phase boundaries** from `docs/IMPROVEMENT_PLAN.md`, not arbitrarily. So Phase 0+1+1polish+2+3 collapse into a single tag; Phase 4 into the next; etc.
- **Every tag is a GitHub Release** with auto-generated notes, not just a bare git tag. Use `gh release create vX.Y.Z --generate-notes`.
- **Pre-release suffixes** (`v2.0.0-rc.1`) for the Phase 5 deferred migrations (AGP 9 / Hilt 2.59 / Kotlin 2.3.20+) so adopters can preview before promotion.
- Old `v1.0.0`–`v1.4.0` tags from August 2025 are pre-roadmap and immutable; they don't map to any current phase. The next tag after Phase 4 wraps will be **`v2.0.0`** (the `minSdk` 25→26 in #105 is breaking for any fork from `v1.4.0`).
