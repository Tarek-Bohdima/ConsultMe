# Template improvement plan

ConsultMe is a Jetpack Compose multi-module Android template. This document is the living roadmap for getting it from "works for me" to "drop-in flexible template that any team can fork in five minutes." Phases are independently shippable; pick them off in any order, but the suggested sequence delivers the most value per PR.

## Status at a glance

| Phase | Theme | State |
|---|---|---|
| 0 | Cleanup & flexibility | **Done** (#97) |
| 1 | Convention plugins (`build-logic/`) | **Done** (#101) |
| 2 | Template ergonomics (bootstrap script, parameterized header) | **Done** (#104) |
| 3 | Real example tests | **Done** (#105) |
| 4 | Production-readiness (R8, CI artifacts, instrumented tests) | **Done** (#107, #113, #114, #115) |
| 5 | Deferred migrations (AGP 9, Hilt 2.59+, Kotlin 2.3.20) | Blocked by upstream pin in `dependabot.yml` |

Tick the table when phases land. Each phase below lists scope, rationale, and a rough size; sub-bullets are the concrete deltas.

---

## Phase 0 — Cleanup & flexibility (done)

Shipped in #97. Goal was to strip migration-era cruft and make the placeholder feature module agnostic so the template doesn't ship as "ChatApp."

- **Rename `:feature-chat` → `:feature-example`.** "Chat" is a domain; "example" is scaffolding intent. Touches `settings.gradle.kts`, `app/build.gradle.kts`, the module dir, the source package (`feature.chat` → `feature.example`), `ChatScreen.kt` → `ExampleScreen.kt`, the import in `MainActivity.kt`, and the README rename steps.
- **Strip migration comments from every `build.gradle.kts`.** The `// <-- ADDED`, `// <-- ENSURED/ADDED`, `// <-- REMOVED (likely not needed)`, `// As per your other files`, etc., are artifacts from when this template was being assembled. They don't describe behavior, only history — and the template's lineage will diverge from forks anyway.
- **Drop commented-out `kapt` lines** in every module + the kapt plugin alias in `libs.versions.toml`. KSP is the only path; the commented-out alternative just adds confusion. Hilt 2.48+ supports KSP fully, so there's no scenario where a template adopter wants kapt.
- **Drop the dead Spotless exclude** `**/camera/viewfinder/**` (no such path exists in this project).
- **Drop the deprecated `android.nonFinalResIds=false`** from `gradle.properties` — AGP marks it as removed in v10. Default is `true` (non-final, faster builds).
- **Make `hilt { enableAggregatingTask = true }` consistent** across every module that applies the Hilt plugin. Currently only `:app`, `:core-ui`, and `:feature-chat` set it; `:core-data` and `:core-database` rely on defaults. For a template, explicit beats implicit.
- **Fix the README typo** "Detect" → "Detekt" (two occurrences, lines 14 and 51).
- **Replace the bug-report issue template.** Current one is a generic copy-paste asking about iOS and browsers — irrelevant to an Android template. Rewrite with Android device, OS version, AGP/Kotlin/JDK, repro steps.

> A sensible `.editorconfig` already exists at the repo root (Compose-aware ktlint config). No change needed in Phase 0.

Out of scope for Phase 0 (intentionally): restructuring build scripts, parameterizing the license header, ProGuard, CI changes. Those each have their own phase.

## Phase 1 — Convention plugins (done)

Goal: eliminate the duplicated Gradle config in every module so adding a feature is a near-one-liner.

Shipped as a `build-logic/` included build with four precompiled script plugins, composed per module:

- `consultme.android.application` — AGP-app + kotlin + JVM 17 / `-Xcontext-receivers` + compileSdk/minSdk/lint defaults + Hilt test runner + release proguard wiring.
- `consultme.android.library` — same scope minus app concerns + `consumerProguardFiles`.
- `consultme.android.compose` — compose-compiler plugin + `buildFeatures.compose = true` + Compose BOM + ui/graphics/tooling-preview/material3 deps.
- `consultme.android.hilt` — hilt-gradle + ksp plugins + `enableAggregatingTask = true` + `hilt-android` impl + `hilt-compiler` ksp.

How modules compose them:
- `:core-testing` — `library` only.
- `:core-data`, `:core-database` — `library + hilt`.
- `:core-ui`, `:feature-example` — `library + compose + hilt`.
- `:app` — `application + compose + hilt`.

A feature module's build script now reads ~20 lines: `plugins {}`, `namespace`, module-specific dependencies. Total scripts shrunk from ~530 to ~190 lines.

Notes for the next contributor:
- Plugin sources live under `build-logic/convention/src/main/kotlin/`. Shared helpers (`configureKotlinAndroid`, `configureBuildFeatures`, `configureLint`) live in `com.thecompany.consultme.buildlogic`.
- The version catalog (`libs`) is exposed inside precompiled script plugins via the workaround documented on `build-logic/convention/build.gradle.kts` (Gradle issue #15383).
- Resist a `feature` mega-plugin — two or three composable plugins beat one with twelve flags.

## Phase 1 polish (done)

Two ergonomic wins layered onto the convention plugins, both from [Modexa, "7 Gradle Kotlin DSL Tricks"](https://medium.com/@Modexa/7-gradle-kotlin-dsl-tricks-for-human-friendly-builds-68506270906f) (tricks #3 and #6):

- **Type-safe project accessors** — `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` in `settings.gradle.kts`; modules now use `projects.coreUi` etc. instead of `project(":core-ui")`. Required renaming `rootProject.name` from `"Consult Me"` to `"ConsultMe"` (build identifier; user-facing app name in `strings.xml` is unchanged).
- **Bundles for Compose + testing dependencies** — `[bundles]` entries in `gradle/libs.versions.toml`: `androidx-compose`, `androidx-compose-debug`, `test-shared`. The compose convention plugin's dep list shrunk from 10 lines to 6; `:core-testing`'s 11 `api()` lines collapsed to one `api(libs.bundles.test.shared)`. The Compose BOM stays unbundled because `platform(...)` can't wrap a bundle.

## Phase 2 — Template ergonomics (done)

Goal: turn the manual "find/replace these 8 places" rename ritual into one command and parameterize the company name.

Shipped in this PR:

- **`scripts/rename-template.py com.acme.myapp "My App Name"`** — single-pass rewrite of package (namespace, applicationId, source dirs, package/import statements), project identifier (rootProject.name, `Theme.<name>`, `<name>Theme` composable, `<name>Application` class + filename), convention plugin slug (`consultme.android.*` → `<lower>.android.*`), and app display name. Implemented in Python 3 for cross-platform `sed -i` safety; the file is `.py` rather than `.sh` because the spec explicitly endorsed either. Re-running with the same args is a no-op.
- **Parameterized Spotless license header** via two `gradle.properties` keys (`template.company`, `template.licenseYear`). Defaults preserve existing `// Copyright $YEAR MyCompany` output; adopters override `template.company` once per fork.
- **README rewrite** — the rename section now points at the script first; manual steps are an appendix (`#manual-rename-fallback`). Added "How to add a new feature module," "How to write a Hilt-aware test," and "How to regenerate lint baselines" sections.

## Phase 3 — Real example tests

Goal: replace the empty `ExampleUnitTest` shells with code that doubles as documentation for "how to test in this template."

- `:core-testing` exposes a small `HiltTestRule` helper composing `HiltAndroidRule` + `MainDispatcherRule`.
- `:feature-example` ships a real `ExampleViewModel` exposing a `StateFlow`, plus:
  - A unit test using Turbine to assert flow emissions.
  - A Hilt-injected test using `core-testing`'s runner.
  - A Compose UI test asserting the screen renders.
- Each test stays small (≤ 30 lines) — the goal is exemplification, not coverage.

## Phase 4 — Production-readiness (done)

Goal: a fork from this template should be one signing config away from a Play release. Split across four PRs so each piece could be tuned independently.

**CI sub-piece (#107):**
- `assembleRelease` step in `build_and_test` (catches APK build regressions `test` misses).
- Upload `*/build/reports/lint-results-*.html` and `*/build/reports/tests/**` after every run (`if: always()` so they survive failures).
- New parallel `instrumented_tests` job runs the GMD profile registered in #105 (`./gradlew pixel6api30DebugAndroidTest`). Includes KVM enable, AVD cache, and instrumented-test report upload. Not yet wired into branch protection — opt in via the repo's required-checks settings once the job has run a few times reliably.
- Workflow now uses a `concurrency` group so superseded commits cancel mid-flight.

**R8 sub-piece (this PR):**
- `isMinifyEnabled = true` and `isShrinkResources = true` for the release build type in the `consultme.android.application` convention plugin. Every adopter inherits R8 + resource shrinking on release without having to touch their app module.
- `:app/proguard-rules.pro` rewritten as a slim starter — the only project-specific rule is `-keepattributes SourceFile,LineNumberTable` (with the matching `-renamesourcefileattribute SourceFile`) so production crashes symbolicate via the `mapping.txt` AGP writes under `build/outputs/mapping/release/`. The platform defaults (`proguard-android-optimize.txt`) plus AAR-shipped consumer rules from Hilt, Compose, Room, and kotlinx.coroutines cover the rest. Comments document when adopters need to add their own keeps (reflective serializers, `Class.forName`, etc.) and point at the [`r8-analyzer`](https://github.com/android/skills) Claude Code skill.
- Validated locally: `:app:assembleRelease` ships a 896 KB unsigned release APK; `:app:lintRelease` and `./gradlew test` are clean.

**Community docs sub-piece (#114):**
- `CONTRIBUTING.md` at the repo root — scope statement, local setup, the local CI loop, PR conventions (Conventional Commits, branch protection, one-scope-per-PR), license-header note, repo layout, bug/security reporting paths.
- `.github/PULL_REQUEST_TEMPLATE.md` — Summary / Test plan / Reviewer notes / Refs sections, mirroring the format the recent PR queue has been using.
- `.github/ISSUE_TEMPLATE/config.yml` — disables blank issues and surfaces two contact links (the roadmap, and the security-disclosure path documented in `CONTRIBUTING.md`).

**Code of conduct sub-piece (this PR):**
- `CODE_OF_CONDUCT.md` at the repo root — verbatim Contributor Covenant v2.1, fetched from the canonical source. The enforcement-contact slot routes to GitHub's "Report a vulnerability" private flow (the same channel `SECURITY.md` documents in #116) — no email is required anywhere in the project.
- `CONTRIBUTING.md` re-links the CoC under a "Code of conduct" section, and the "Reporting bugs and security issues" section drops the prior "email the maintainer" wording in favor of the same private Security-tab flow.

## Phase 5 — Deferred migrations

Currently silenced in `.github/dependabot.yml`. Each is its own dedicated PR, not a passive bot bump:

- **AGP 8.x → 9.x.** Drops the `kotlin-android` plugin requirement and forces Gradle ≥ 9.4. Touches every module's plugin block. Requires removing `alias(libs.plugins.kotlin.android)` and verifying Compose/KSP/Hilt all play nicely with AGP 9's bundled Kotlin support. The official [`agp-9-upgrade`](https://github.com/android/skills) Claude Code skill is a ready-made playbook for this migration.
- **Hilt 2.59+.** Hard-requires AGP 9; do this in the same PR or a follow-up to the AGP 9 migration.
- **Kotlin 2.3.20+.** Promotes `-Xcontext-receivers` from a warning to a hard error in release variants — `compileReleaseKotlin` fails in `:core-testing` and the feature module. Both 2.3.20 and 2.3.21 hit this. Unpinning is gated on the `-Xcontext-receivers` → `-Xcontext-parameters` migration below.

## Known follow-ups not yet phased

- **`-Xcontext-receivers` is deprecated** (every module sets this in `freeCompilerArgs`). Kotlin compiler warns: replace with `-Xcontext-parameters` and migrate to the new syntax. Already a hard error on Kotlin 2.3.20+ release variants — **this migration is what unblocks the Kotlin 2.3.20+ pin in `dependabot.yml`**. The migration is mechanical, but worth verifying nothing in Compose/Hilt-generated code uses receivers.
- **KSP `2.3.4` lags Kotlin `2.3.10`.** Dependabot will likely propose a bump on the next run; let it.

## Quality bets to consider (no phase yet)

These are valuable but don't fit cleanly into the above. Worth deciding on before Phase 4:

- **Kover** for coverage with HTML/XML reports. Add to `:core-testing` consumers.
- **Baseline profiles + macrobenchmark module** for startup performance.
- **`dependency-analysis-android-gradle-plugin`** to flag unused/misplaced dependencies — high signal for multi-module hygiene.
- **Gradle build scans** opt-in via `develocity { server = ... }` if there's a Develocity instance available.
- **Edge-to-edge layouts.** Modern Android (16+) effectively requires opt-in; the template currently doesn't enforce it. The official [`edge-to-edge`](https://github.com/android/skills) Claude Code skill is a ready-made adoption guide.

## Recommended Claude Code skills

Google maintains official AI-optimized skills for Android at <https://github.com/android/skills>. Skills relevant to this template's roadmap:

| Skill | Use when |
|---|---|
| [`agp-9-upgrade`](https://github.com/android/skills) | Working on Phase 5 (AGP 8 → 9 migration) |
| [`r8-analyzer`](https://github.com/android/skills) | Working on Phase 4 (release minification, keep rules) |
| [`edge-to-edge`](https://github.com/android/skills) | Adopting edge-to-edge (see Quality bets) |

Skills are not vendored into the template — they're maintained upstream by Google. Install them locally when starting the matching phase.

## How to use this document

When starting a phase, change its row in the status table to **In progress**, link the PR, and update sub-bullets with checkboxes if it helps. When merged, mark **Done** with the PR number. The point is to keep the next-best action obvious to whoever opens the repo a month from now.

## Releases

Cut a GitHub Release at every phase boundary, not arbitrarily. Phase 0–3 collapse into one MAJOR (`v2.0.0`, because Phase 3's `minSdk` 25→26 is breaking for adopters); Phase 4 ships as `v2.1.0` (or another MAJOR if R8 introduces required keep-rule changes for forks); Phase 5 ships as `v3.0.0` since AGP 9 / Hilt 2.59 / Kotlin 2.3.20+ each force adopter migrations. See `CLAUDE.md` "Versioning and tags" for the full policy.
