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
| 5 | NIA-alignment slice 1 (convention plugins expansion, drop Detekt) | **Done** (#123) |
| 6 | NIA-alignment slice 2 (module scaffolding: `:core-designsystem`, `:core-model`, `:core-common`, `:core-domain`) | **Done** (#124) |
| 7 | NIA-alignment slice 3 (quality tooling: Kover + module-graph) | **Done** (#126) |
| 8 | NIA-alignment slice 4 (`:baselineprofile` macrobenchmark + baseline profile) | **Done** (#129, `v3.1.0`) |
| 9 | Deferred migrations (AGP 9, Hilt 2.59+, Kotlin 2.3.20) | **Done** (#132, `v4.0.0-rc.1` → `v4.0.0`) |
| 10 | Adopter friction polish (real-world port findings) | **Done** (#144–#157, `v4.1.0`) |

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

## Phase 5 — NIA-alignment slice 1 (convention plugins, drop Detekt)

Goal: bring the `build-logic/` plugin set up to parity with [android/nowinandroid](https://github.com/android/nowinandroid)'s `build-logic/` so future feature/core modules cost ~5 lines of Gradle, and remove redundant tooling.

Shipped:

- **New convention plugins** under `build-logic/convention/src/main/kotlin/`:
  - `consultme.android.feature` — composes `library + compose + hilt` and adds the standard feature deps (lifecycle-runtime-compose, lifecycle-viewmodel-compose, hilt-navigation-compose, `:core-testing` for unit + instrumented tests). Drops a feature-module build script from ~25 lines to ~10.
  - `consultme.android.room` — applies KSP + Room runtime/ktx/compiler + sets `room.schemaLocation` to `$projectDir/schemas`. Migrated `:core-database` to use it.
  - `consultme.android.test` — `com.android.test`, ready for the upcoming `:benchmarks` module in Phase 8.
  - `consultme.android.lint` — `com.android.lint` for pure-Kotlin modules contributing custom Lint checks.
  - `consultme.jvm.library` — pure-Kotlin module (no AGP), ready for upcoming `:core-model` / `:core-domain` modules.
- **Drop Detekt.** NIA stack is `Spotless + ktlint + Android Lint`; Detekt overlaps ~80% with ktlint and adds a 750-line config to maintain. Removed: `detekt`/`config/detekt.yml`, the root `subprojects` Detekt block, the CI step, the dependabot group entry, and the PR-template checkbox. Gates are now `spotlessCheck` + `lintRelease` + `test`.
- **Cleanup.** `enableAggregatingTask = true` in `consultme.android.hilt` was redundant — Hilt 2.55+ enables it by default.

Tracking: #122.

## Phase 6 — NIA-alignment slice 2 (module scaffolding)

Goal: introduce the canonical NIA module split so adopters know where each kind of code belongs without reinventing the layout.

Shipped:

- **`:core-designsystem`** — owns the Compose theme. `ConsultMeTheme`, `Color.kt`, `Type.kt` moved out of `:app/ui/theme/` into `com.thecompany.consultme.core.designsystem.theme`. The `consultme.android.feature` convention plugin adds it as `implementation` so every feature module gets the theme without re-wiring.
- **`:core-model`** — pure-Kotlin module via `consultme.jvm.library`, scaffold for domain data classes. No Android, no Hilt.
- **`:core-common`** — pure-Kotlin module shipping NIA's `Dispatcher` qualifier + `AppDispatchers` enum. Adopters provide the `@Provides` binding wherever they apply Hilt (typically `:app` or a data-layer module).
- **`:core-domain`** — pure-Kotlin module via `consultme.jvm.library`, scaffold for use-cases. Depends on `:core-model`.
- **Convention plugin update** — `consultme.android.feature` now adds `:core-designsystem` and `:core-ui` as `implementation`, mirroring NIA's `AndroidFeatureConventionPlugin`.

`:app` now depends on `:core-designsystem` directly (so `MainActivity` can `import …core.designsystem.theme.ConsultMeTheme`). Tracking: #122.

## Phase 7 — NIA-alignment slice 3 (quality tooling)

Goal: ship the quality-reporting layer adopters expect from a modern modular project.

Shipped:

- **Kover** for aggregated test coverage. Convention plugin `consultme.kover` is auto-applied by every Android (`application`, `library`) and JVM (`jvm.library`) convention. Generated Hilt/Room/Compose code is excluded so the metric reflects real coverage, not noise. `./gradlew koverHtmlReport` and `./gradlew koverXmlReport` produce project-wide reports under `build/reports/kover/`. CI uploads the reports as artifacts.
- **Module-graph generation** with a **Strategy-pattern renderer**. `:moduleGraph` walks every subproject's `api` / `implementation` / `compileOnly` / `runtimeOnly` configurations to build a [`ModuleGraph`](../build-logic/convention/src/main/kotlin/com/thecompany/consultme/buildlogic/ModuleGraph.kt), then delegates rendering to a [`ModuleGraphRenderer`](../build-logic/convention/src/main/kotlin/com/thecompany/consultme/buildlogic/ModuleGraphRenderer.kt). A [`MermaidModuleGraphRenderer`](../build-logic/convention/src/main/kotlin/com/thecompany/consultme/buildlogic/MermaidModuleGraphRenderer.kt) ships by default; adopters wanting DOT/ASCII/JSON drop in their own implementation. Output lands at [`docs/MODULE_GRAPH.md`](MODULE_GRAPH.md). CI runs the task and fails the build if the committed graph is stale.

Deferred from this slice (tracked separately):

- **`dependency-analysis-gradle-plugin`** — both 2.x and 3.x lines fail on the current Kotlin 2.3 + AGP 8.13 combo (2.x errors on Kotlin 2.3 metadata; 3.x errors on Android-test-runtime graph resolution for `:core-testing`). Will reland once an upstream version supports both. The `consultme.modulegraph` Strategy seam already covers part of dep-analysis's value (visualizing inter-module deps). Tracking: #122.

Tracking: #122.

## Phase 8 — NIA-alignment slice 4 (baseline profile + macrobenchmark)

Goal: every fork of the template starts with a baseline-profile pipeline pre-wired, so adopters get the AOT-compilation startup win (~15–30% on cold launches) without reading the AndroidX docs first.

Shipped:

- **`:baselineprofile` module** — `com.android.test` producer that owns:
  - [`BaselineProfileGenerator`](../baselineprofile/src/main/kotlin/com/thecompany/consultme/baselineprofile/BaselineProfileGenerator.kt) — runs once via `BaselineProfileRule` to produce `app/src/main/baseline-prof.txt`.
  - [`StartupBenchmarks`](../baselineprofile/src/main/kotlin/com/thecompany/consultme/baselineprofile/StartupBenchmarks.kt) — cold-start macrobenchmark with two compilation modes (`None` vs `Partial(Require)`); the delta is the win the profile buys.
- **`consultme.android.baselineprofile` convention plugin** — composes the existing `consultme.android.test` (Phase 5) with `androidx.baselineprofile` and the macro+uiautomator deps. New benchmark modules become a one-liner.
- **`:app` consumer wiring** — `consultme.android.application` applies the `androidx.baselineprofile` plugin, and `:app/build.gradle.kts` declares `baselineProfile(projects.baselineprofile)` plus the `androidx.profileinstaller` runtime dep so the profile actually loads on user devices.
- **Shared GMD** — `consultme.android.test` now calls `configureManagedDevices()`, so the same `pixel6api30 / aosp-atd` device used by the existing instrumented-test job runs the profile generator and macrobenchmarks. One AVD entry-point to install/cache.
- **Module-graph plugin** now treats `baselineProfile` as a graphable configuration so the producer→consumer link shows up in `docs/MODULE_GRAPH.md`.

Generation strategy: profile is regenerated **on demand** by adopters (`./gradlew :app:generateReleaseBaselineProfile`), not on every CI run. The committed `app/src/main/baseline-prof.txt` is the canonical artifact. Same trade-off as `MODULE_GRAPH.md` but in reverse: deterministic enough to commit, expensive enough to regenerate sparingly. CI just verifies the module compiles.

Note on design patterns: the AndroidX `BaselineProfileRule` already plays a Template Method role internally (collect → measure → write profile); adding a parallel abstraction on top would only obscure it. The convention plugin provides the only abstraction worth having here, by composing `consultme.android.test` with the AndroidX plugin.

Tracking: #122.

## Phase 8 follow-ups not yet phased

- Initial baseline profile bytes need to be generated locally before forks see startup benefits. The `app/src/main/baseline-prof.txt` file is committed as a stub on the first run after a real device run. Document in CONTRIBUTING.md.

## Phase 9 — Deferred migrations (done)

Goal: pull the template onto the AGP 9 / Hilt 2.59+ tooling line. Kotlin 2.3.20+ already unpinned in #118.

Shipped (this PR):

- **AGP 8.13.2 → 9.2.1** following Google's [`agp-9-upgrade`](https://github.com/android/skills) skill. Steps applied:
  1. **Built-in Kotlin** — removed every `id("org.jetbrains.kotlin.android")` apply, the `kotlin-android` plugin alias from `gradle/libs.versions.toml`, and `alias(libs.plugins.kotlin.android) apply false` from the root build script.
  2. **New AGP DSL** — stripped `<*, *, *, *, *, *>` type parameters from `CommonExtension`; switched library convention's import to `com.android.build.api.dsl.LibraryExtension`; rewrote `lint { … }` block helper to `lint.apply { … }` with the trimmed property surface (only `baseline` and `quiet` survive the AGP 9 cut); rewrote `ManagedVirtualDevice` creation as `allDevices.create<ManagedVirtualDevice>(...)`; replaced `KotlinAndroidProjectExtension` config with `tasks.withType<KotlinCompile>` configuration since built-in Kotlin no longer registers a project-level Kotlin extension.
  3. **gradle.properties cleanup** — removed `android.builtInKotlin`, `android.newDsl`, `android.uniquePackageNames`, `android.enableAppCompileTimeRClass` per skill Step 6.
  4. **Library proguard refs** — dropped `consumerProguardFiles("consumer-rules.pro")` and `proguardFiles(..., "proguard-rules.pro")` from `consultme.android.library` since `android.proguard.failOnMissingFiles=true` is the new default; libraries that need consumer rules now declare them in their own build script.
- **Hilt 2.58 → 2.59.2** — bundled in the same PR per `CLAUDE.md` guidance.
- **Kover 0.9.1 → 0.9.8** — needed for AGP 9 compatibility.
- **`androidx.baselineprofile` 1.4.1 → 1.5.0-alpha06** — only line that supports AGP 9; alpha-pinning a template is suboptimal but the alternative (commenting out `:baselineprofile`) regresses Phase 8. Will track 1.5.x stable when it lands.

Catalog cleanup:

- Removed the `kotlin-android = { id = "...", version.ref = "kotlin" }` plugin alias from `[plugins]`.
- Lifted the AGP semver-major + Hilt 2.59+ ignore entries from `.github/dependabot.yml`. Dependabot is now free to bump these along with the rest of the gradle-and-plugins group.

## Phase 10 — Adopter friction polish (done)

Goal: address concrete adopter pain points surfaced by a real-world downstream port (24-PR effort landing a Java/XML/AppCompat app on the template). Every fix corrects a friction point that bit a first-time adopter in the first 24–72 hours of using the template — diacritic-handling bugs in the bootstrap, a Spotless misconfiguration that silently no-op'd license-header rewrites on Gradle scripts, an unsigned cost on private-repo forks, a missing screenshot-test convention, and so on.

Shipped:

- **Bootstrap script hardening** (#145) — `to_pascal()` now NFD-normalizes and strips combining marks so `"Ce gătesc?"` produces `CeGatesc` instead of `CeGTesc`; `rename_plugin_files` and the text replacement broaden to every `consultme.*` slug so `consultme.jvm.library`, `consultme.kover`, and `consultme.modulegraph` get renamed alongside the `.android.*` plugins. First-ever pytest suite + Python CI workflow added to catch regressions.
- **Spotless `kotlinGradle` delimiter fix** (#146) — the previous delimiter `"/*"` parses as the regex "zero-or-more '/'", which matched everywhere and made Spotless silently skip every `.gradle.kts` header rewrite. Switched to `plugins \{`, which every subproject `build.gradle.kts` has on its first non-header line.
- **`consultme.android.roborazzi` convention plugin** (#149) — one-line opt-in for JVM Compose snapshot testing. Wires the Roborazzi plugin + deps + the `includeAndroidResources` flag, plus Compose BOM and `ui-test-junit4` for `createComposeRule`. Catalog pins Roborazzi `1.56.0` (the first version with AGP 9 support) and Robolectric `4.15.1` at ship time; both have since been bumped by Dependabot to Roborazzi `1.60.0` (#163) and Robolectric `4.16.1` (#164). Ships with a worked example test (`@Ignore`'d until baselines are recorded).
- **`configureUnitTests` helper** (#154) — extracted the AGP 9 `findByType<LibraryExtension>` / `findByType<ApplicationExtension>` dance from the Roborazzi convention into a reusable `Project.configureUnitTests()` helper on `AndroidExtensions.kt`. Documents the dropped `CommonExtension<*, *, *, *, *, *>` generic shape so the next contributor doesn't trip over it.
- **CodeQL private-repo gate** (#150) — `analyze` job now gates on `github.event.repository.private == false`. Private-repo forks without GitHub Advanced Security stop wasting ~3 min per PR per language on failing scans.
- **`:feature-example` placeholder warnings** (#147) — TODO comment on `projects.featureExample` dep line in `app/build.gradle.kts` + `feature-example/README.md` explaining the removal steps. Keeps the worked example as scaffolding without letting it accidentally ship.
- **`:core-domain` / `:core-ui` / `:core-common` orientation READMEs** (#151) — one paragraph per module on role + what belongs / doesn't belong, so adopters opening the dir tree know what each scaffold module is for without grepping `MODULE_GRAPH.md`.
- **`:baselineprofile` flavor caveat** (#155) — multi-line comment on `TARGET_PACKAGE` in both `BaselineProfileGenerator.kt` and `StartupBenchmarks.kt` + a new `baselineprofile/README.md` documenting the per-flavor override workflow. (Considered runtime derivation via `InstrumentationRegistry`; doesn't apply for macrobenchmark since the test runs in a separate process from the benchmarked app.)
- **Bootstrap deletes upstream `IMPROVEMENT_PLAN.md`** (#153) — same pattern as `.github/FUNDING.yml`. Gated on the upstream sentinel header so an adopter's own roadmap doc is untouched on re-runs.
- **README adopter sections** (#152) — "Using vector icons" (callout, not bundling, for `material-icons-extended`) + "Adding product flavors" (the `defaultConfig.applicationId` override semantics that real adopters trip on, with a worked `productFlavors {}` snippet).
- **`google-services.json` gitignore softening** (#148) — replaced "CRITICAL TO IGNORE" with a softer comment + a commented-out `!app/google-services.json` example so private-repo adopters who need flavor-driven Firebase client selection don't have their `git add` silently skipped.
- **`createComposeRule` v2 migration** (#157) — the example snapshot test added in #149 imported the deprecated v1 createComposeRule. Migrated to `androidx.compose.ui.test.junit4.v2.createComposeRule` so adopters who copy-paste the example don't carry the deprecation forward.
- **Repository hygiene** — `__pycache__/` and `.pytest_cache/` added to `.gitignore` (#144, #156).

Not shipped (pushback accepted):

- **Auto-bundle `material-icons-extended`** — would have added ~1.5 MB across every adopter to cover an opt-in need. Shipped as a README callout instead (#152).
- **Bootstrap-script deletes `:feature-example`** — too aggressive. Adopters use the placeholder as scaffolding while writing their first real feature module. Loud warnings + module README (#147) achieve the same end without removing the worked example.
- **Bootstrap rewrites `IMPROVEMENT_PLAN.md` to an empty stub** — extra ceremony for no benefit. Plain delete matches the existing `.github/FUNDING.yml` handling (#153).
- **Runtime derivation of `TARGET_PACKAGE`** for `:baselineprofile` — `InstrumentationRegistry.targetContext.packageName` doesn't reach across to the benchmarked app's process in macrobenchmark. Documenting the per-flavor override was the simpler, correct answer (#155).

## Phase 9 — original (kept for history)

Currently silenced in `.github/dependabot.yml`. Each is its own dedicated PR, not a passive bot bump:

- **AGP 8.x → 9.x.** Drops the `kotlin-android` plugin requirement and forces Gradle ≥ 9.4. Touches every module's plugin block. Requires removing `alias(libs.plugins.kotlin.android)` and verifying Compose/KSP/Hilt all play nicely with AGP 9's bundled Kotlin support. The official [`agp-9-upgrade`](https://github.com/android/skills) Claude Code skill is a ready-made playbook for this migration.
- **Hilt 2.59+.** Hard-requires AGP 9; do this in the same PR or a follow-up to the AGP 9 migration.

## Recently shipped follow-ups

- **`-Xcontext-receivers` → `-Xcontext-parameters`** (tracked in issue #117). The flag is set in one place — `build-logic/.../AndroidExtensions.kt` — and no source code uses `context(...)` syntax, so the migration was a one-line swap. This unblocked the `org.jetbrains.kotlin.*` `>=2.3.20` pin in `.github/dependabot.yml`; the next bot run can propose the Kotlin bump.

## Quality bets to consider (no phase yet)

These are valuable but don't fit cleanly into the above. Worth deciding on before Phase 4:

- **Kover** for coverage with HTML/XML reports. Add to `:core-testing` consumers.
- **Baseline profiles + macrobenchmark module** for startup performance.
- **`dependency-analysis-android-gradle-plugin`** to flag unused/misplaced dependencies — high signal for multi-module hygiene.
- **Gradle build scans** opt-in via `develocity { server = ... }` if there's a Develocity instance available.
- **Edge-to-edge layouts.** Modern Android (16+) effectively requires opt-in; the template currently doesn't enforce it. The official [`edge-to-edge`](https://github.com/android/skills) Claude Code skill is a ready-made adoption guide.

## Recommended Claude Code skills

Google maintains official AI-optimized skills for Android at <https://github.com/android/skills>. The upstream catalog has grown to 16 skills as of v0.0.10 (2026-05-15); the ones relevant to this template's roadmap and to adopters porting real features on top of it:

**Phase migrations** (template-author work):

| Skill | Use when |
|---|---|
| [`build/agp/agp-9-upgrade`](https://github.com/android/skills) | Phase 9 (AGP 8 → 9 migration); kept for AGP 10 prep |
| [`performance/r8-analyzer`](https://github.com/android/skills) | Phase 4 (release minification, keep rules) |
| [`system/edge-to-edge`](https://github.com/android/skills) | Adopting edge-to-edge (see Quality bets) |

**Adopter-facing** (downstream-fork work after they take the template):

| Skill | Use when |
|---|---|
| [`testing/testing-setup`](https://github.com/android/skills) | Wiring tests on top of `:core-testing` |
| [`jetpack-compose/theming/styles`](https://github.com/android/skills) | Extending `:core-designsystem` (icons, typography, components) |
| [`jetpack-compose/migration/migrate-xml-views-to-jetpack-compose`](https://github.com/android/skills) | Porting a legacy Views codebase onto the Compose template |
| [`navigation/navigation-3`](https://github.com/android/skills) | Growing a real nav graph beyond `:feature-example` |
| [`profilers/perfetto-sql`](https://github.com/android/skills), [`profilers/perfetto-trace-analysis`](https://github.com/android/skills) | Investigating startup regressions alongside `:baselineprofile` |

Skills are not vendored into the template — they're maintained upstream by Google. Install them locally when starting the matching work. Keep this list in sync with `CLAUDE.md`'s "Recommended Claude Code skills" section.

## How to use this document

When starting a phase, change its row in the status table to **In progress**, link the PR, and update sub-bullets with checkboxes if it helps. When merged, mark **Done** with the PR number. The point is to keep the next-best action obvious to whoever opens the repo a month from now.

## Releases

Cut a GitHub Release at every phase boundary, not arbitrarily. Actual phase → release mapping:

- **Phases 5–7** (NIA-alignment slices 1–3) bundled into `v3.0.0`. MAJOR because slice 2 moved the theme package (`com.thecompany.consultme.ui.theme` → `core.designsystem.theme`) — breaking for forks at v2.x.
- **Phase 8** (`:baselineprofile` macrobenchmark + pipeline) shipped as `v3.1.0` (additive, non-breaking).
- **Phase 9** (deferred migrations: AGP 9.2.1 / Hilt 2.59.2) shipped as `v4.0.0-rc.1`, promoted to `v4.0.0` on 2026-05-10. The `androidx.baselineprofile` 1.5.x stable concern that originally blocked promotion was accepted as a known alpha pin.
- **Phase 10** (adopter friction polish from real-world port findings) shipped as `v4.1.0` (additive: new `consultme.android.roborazzi` convention plugin + bug fixes + adopter-facing docs; non-breaking).

See `CLAUDE.md` "Versioning and tags" for the full policy.
