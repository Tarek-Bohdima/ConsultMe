# Template improvement plan

ConsultMe is a Jetpack Compose multi-module Android template. This document is the living roadmap for getting it from "works for me" to "drop-in flexible template that any team can fork in five minutes."

Phases 0–10 are all shipped. The template is now in **maintenance-and-extension** mode: forward-looking work lives at the top of this doc; the full per-phase history is archived (collapsed) at the bottom.

## Status at a glance

| Phase | Theme | State |
|---|---|---|
| 0 | Cleanup & flexibility | **Done** (#97) |
| 1 | Convention plugins (`build-logic/`) | **Done** (#101) |
| 2 | Template ergonomics (bootstrap script, parameterized header) | **Done** (#104) |
| 3 | Real example tests | **Done** (#105) |
| 4 | Production-readiness (R8, CI artifacts, instrumented tests) | **Done** (#107, #113, #114, #115) |
| 5 | NIA-alignment slice 1 (convention plugins expansion, drop Detekt) | **Done** (#123) |
| 6 | NIA-alignment slice 2 (module scaffolding) | **Done** (#124) |
| 7 | NIA-alignment slice 3 (quality tooling: Kover + module-graph) | **Done** (#126) |
| 8 | NIA-alignment slice 4 (`:baselineprofile` macrobenchmark + baseline profile) | **Done** (#129, `v3.1.0`) |
| 9 | Deferred migrations (AGP 9, Hilt 2.59+, Kotlin 2.3.20) | **Done** (#132, `v4.0.0`) |
| 10 | Adopter friction polish (real-world port findings) | **Done** (#144–#157, `v4.1.0`) |

Per-phase detail is archived under [Completed phases (0–10)](#completed-phases-010) at the bottom — the summary above plus the linked PRs are usually enough.

## Recent work (post-Phase 10)

Feature and hygiene work since `v4.1.x`, outside the numbered phases:

- **End-to-end vertical slice** (#227) — populated the previously-empty domain/data layers with one canonical ports-&-adapters slice: `ExampleItem` (`:core-model`) → Room DAO (`:core-database`) → `ExampleRepository` port (`:core-domain`) / `DefaultExampleRepository` adapter (`:core-data`) → `GetExampleItemsUseCase` → `ExampleViewModel`. Replaced placeholder test stubs with real per-layer tests, and reconciled `ARCHITECTURE.md` with the actual module graph.
- **Navigation 3** (#229) — migrated `:app` off the (declared-but-unused) Nav2 `navigation-compose` to Navigation 3 (stable `1.1.4`): app-owned `NavDisplay`, `@Serializable` `NavKey` routes owned by features, and a list→detail flow whose detail ViewModel uses Hilt assisted injection for the route argument.
- **Dependency maintenance** — AGP 9.3.0 (#222), Hilt 2.60.1 (#214), kover 0.9.9 + spotless 8.8.0 (#224), plus the roborazzi / uiautomator / androidx / github-actions Dependabot groups.

## Deferred / blocked

Live constraints. Each is a coordinated, dedicated PR (not a passive bot bump) and is pinned in `.github/dependabot.yml` until it unblocks.

- **Kotlin 2.4.x bump — blocked on KSP + Hilt + CodeQL** (tracked in issue #185; pinned via a `.github/dependabot.yml` ignore on `org.jetbrains.kotlin* >=2.4.0`). Dependabot's Kotlin `2.3.21 → 2.4.x` bumps (#184, #189, #198, #220, #221) fail at `:app:hiltJavaCompileDebug` with `[Hilt] Provided Metadata instance has version 2.4.0, while maximum supported version is 2.3.0` — Hilt's bundled `kotlin-metadata-jvm` is not yet Kotlin-2.4-aware (re-confirmed 2026-07-17: Hilt 2.60.1 still bundles Kotlin 2.3.x; since Dagger 2.57 the artifact is unshaded, so a `resolutionStrategy.force("org.jetbrains.kotlin:kotlin-metadata-jvm:<2.4-capable>")` is a possible manual override once the rest unblocks). Also blocked on **KSP** — latest is `2.3.10` (Kotlin-2.3 line); no `2.4.x` KSP exists yet (google/ksp#2965 open), and KSP must match the Kotlin compiler line. And on **CI CodeQL** — the java-kotlin extractor errors `Kotlin version 2.4.10 is too recent; supports versions below 2.4.10` (CodeQL 2.26.0 added "up to 2.4.0"; 2.4.10 not yet supported). Lands as a coordinated `kotlin` + `ksp` + `hilt` bump on the `v4.x-rc` pre-release channel once KSP ships `2.4.x`, a Hilt release bumps bundled metadata to 2.4, and CodeQL supports 2.4.10. Same shape as the Phase 9 deferral.
- **KSP `2.3.9 → 2.3.10` bump — held** (pinned via a `.github/dependabot.yml` ignore on `com.google.devtools.ksp* >2.3.9`). KSP 2.3.10 added an `onlyIf` gate that SKIPs `kspDebug<Variant>AndroidTestKotlin` for library-module `androidTest` source sets, so Hilt test-component codegen never runs — `:feature-example` instrumented tests then fail at runtime with `ClassNotFoundException: …ExampleHiltTest_TestComponentDataSupplier` (#217, and the KSP slice of the gradle-and-plugins group #219). Reproduced locally on 2026-07-17: 2.3.9 runs the KSP androidTest task and emits the Hilt injector/aggregated-deps sources; 2.3.10 skips it (`onlyIf 'Task satisfies onlyIf spec' is false`) and emits nothing. Suspected origin is google/ksp PR #2996 (`@SkipWhenEmpty` / source-tree filtering rework); no fixed release exists yet (2.3.11 unreleased). Filed upstream as **google/ksp#3050**. Drop the ignore once a KSP release restores androidTest processing.

## Quality bets to consider (no phase yet)

Valuable but not yet scheduled:

- **`dependency-analysis-android-gradle-plugin`** to flag unused/misplaced dependencies — high signal for multi-module hygiene (it would, for instance, have caught the dead `navigation-compose` dep that #229 removed). Historically blocked on the stack (Kotlin 2.3-metadata / Android-test-runtime graph resolution for `:core-testing`); latest is 3.16.0, worth re-testing against the current AGP 9.3 + Kotlin 2.3 stack. No open tracking issue.
- **Gradle build scans** — opt-in via `develocity { server = … }` if a Develocity instance is available.
- **Populate `:core-ui`** — still a scaffold. Extracting the example screen's loading/empty composables into a reusable `:core-ui` component would give it its first real content.

Already shipped (kept here only so they're not re-proposed): Kover (Phase 7), baseline profiles + macrobenchmark (Phase 8), edge-to-edge (wired from the start via `enableEdgeToEdge()` + inset-consuming `Scaffold` in `MainActivity`).

## Recommended Claude Code skills

Google maintains official AI-optimized skills for Android at <https://github.com/android/skills>. Skills are **not vendored** — install locally when starting the matching work. The ones relevant to this template:

**Phase migrations** (template-author work):

| Skill | Use when |
|---|---|
| `build/agp/agp-9-upgrade` | AGP 8 → 9 (Phase 9); kept for AGP 10 prep |
| `performance/r8-analyzer` | Release minification, keep rules (Phase 4) |
| `system/edge-to-edge` | Extending inset handling |

**Adopter-facing** (downstream-fork work):

| Skill | Use when |
|---|---|
| `testing/testing-setup` | Wiring tests on top of `:core-testing` |
| `jetpack-compose/theming/styles` | Extending `:core-designsystem` |
| `jetpack-compose/migration/migrate-xml-views-to-jetpack-compose` | Porting a legacy Views codebase |
| `navigation/navigation-3` | Extending the Nav3 graph shipped in `:app` (more destinations, adaptive `NavigationSuiteScaffold` / `SceneStrategy`) |
| `jetpack-compose/adaptive` | Large screens / foldables (see README "How to support adaptive layouts") |
| `profilers/perfetto-sql`, `profilers/perfetto-trace-analysis` | Startup regressions alongside `:baselineprofile` |

Keep this list in sync with `CLAUDE.md`'s "Recommended Claude Code skills" section.

## Releases

Cut a GitHub Release at every phase boundary (or feature milestone), not arbitrarily. Actual mapping so far:

- **Phases 5–7** (NIA-alignment slices 1–3) → `v3.0.0` (MAJOR: slice 2 moved the theme package, breaking for forks at v2.x).
- **Phase 8** (`:baselineprofile`) → `v3.1.0` (additive).
- **Phase 9** (AGP 9.2.1 / Hilt 2.59.2) → `v4.0.0-rc.1`, promoted to `v4.0.0` on 2026-05-10.
- **Phase 10** (adopter friction polish) → `v4.1.0` (additive).
- **Post-Phase-10 feature work** (vertical slice #227, Navigation 3 #229) is additive and adopter-visible — fold into the next MINOR (`v4.2.0`).

See `CLAUDE.md` "Versioning and tags" for the full policy.

## How to use this document

Forward work goes in **Recent work** / **Deferred / blocked** / **Quality bets**. When you pick up a deferred or quality-bet item, link the PR and move it to Recent work (or a new phase row) when it lands. The point is to keep the next-best action obvious to whoever opens the repo a month from now. Full history for shipped phases is archived below.

---

## Completed phases (0–10)

<details>
<summary>Full per-phase detail (archived — the status table + linked PRs above are the quick reference)</summary>

### Phase 0 — Cleanup & flexibility (done)

Shipped in #97. Goal was to strip migration-era cruft and make the placeholder feature module agnostic so the template doesn't ship as "ChatApp."

- **Rename `:feature-chat` → `:feature-example`.** "Chat" is a domain; "example" is scaffolding intent. Touches `settings.gradle.kts`, `app/build.gradle.kts`, the module dir, the source package (`feature.chat` → `feature.example`), `ChatScreen.kt` → `ExampleScreen.kt`, the import in `MainActivity.kt`, and the README rename steps.
- **Strip migration comments from every `build.gradle.kts`.** The `// <-- ADDED`, `// <-- ENSURED/ADDED`, `// <-- REMOVED (likely not needed)`, `// As per your other files`, etc., are artifacts from when this template was being assembled.
- **Drop commented-out `kapt` lines** in every module + the kapt plugin alias in `libs.versions.toml`. KSP is the only path; Hilt 2.48+ supports KSP fully.
- **Drop the dead Spotless exclude** `**/camera/viewfinder/**` (no such path exists).
- **Drop the deprecated `android.nonFinalResIds=false`** from `gradle.properties` — AGP marks it removed in v10.
- **Make `hilt { enableAggregatingTask = true }` consistent** across every Hilt module. For a template, explicit beats implicit.
- **Fix the README typo** "Detect" → "Detekt".
- **Replace the bug-report issue template** with an Android-relevant one (device, OS, AGP/Kotlin/JDK, repro steps).

Out of scope for Phase 0 (intentionally): restructuring build scripts, parameterizing the license header, ProGuard, CI changes.

### Phase 1 — Convention plugins (done)

Goal: eliminate duplicated Gradle config so adding a feature is a near-one-liner. Shipped as a `build-logic/` included build with four precompiled script plugins:

- `consultme.android.application` — AGP-app + kotlin + JVM 17 + compileSdk/minSdk/lint defaults + Hilt test runner + release proguard wiring.
- `consultme.android.library` — same scope minus app concerns + `consumerProguardFiles`.
- `consultme.android.compose` — compose-compiler plugin + `buildFeatures.compose` + Compose BOM + ui/graphics/tooling-preview/material3.
- `consultme.android.hilt` — hilt-gradle + ksp plugins + `hilt-android` impl + `hilt-compiler` ksp.

A feature module's build script now reads ~20 lines. Total scripts shrunk from ~530 to ~190 lines. Plugin sources live under `build-logic/convention/src/main/kotlin/`; shared helpers in `com.thecompany.consultme.buildlogic`. The version catalog is exposed inside precompiled scripts via the workaround documented on `build-logic/convention/build.gradle.kts` (Gradle issue #15383). Resist a `feature` mega-plugin.

**Phase 1 polish:** type-safe project accessors (`projects.coreUi` — required renaming `rootProject.name` to `"ConsultMe"`), and `[bundles]` for Compose + testing deps (`:core-testing`'s 11 `api()` lines collapsed to one `api(libs.bundles.test.shared)`).

### Phase 2 — Template ergonomics (done, #104)

- **`scripts/rename-template.py com.acme.myapp "My App Name"`** — single-pass rewrite of package, project identifier, convention plugin slug, and app display name. Python 3 for cross-platform safety; re-running with the same args is a no-op.
- **Parameterized Spotless license header** via `template.company` / `template.licenseYear` in `gradle.properties`.
- **README rewrite** — rename section points at the script first; manual steps are an appendix.

### Phase 3 — Real example tests (done, #105)

- `:core-testing` exposes `HiltTestRule` (composes `HiltAndroidRule` + `MainDispatcherRule`).
- `:feature-example` ships a real `ExampleViewModel` + `StateFlow`, a Turbine unit test, a Hilt-injected test, and a Compose UI test. Each test ≤ ~30 lines — exemplification, not coverage.

### Phase 4 — Production-readiness (done)

- **CI (#107):** `assembleRelease` in `build_and_test`; report uploads with `if: always()`; parallel `instrumented_tests` GMD job (`pixel6api30DebugAndroidTest`) with KVM/AVD cache; `concurrency` group cancels superseded commits.
- **R8:** `isMinifyEnabled`/`isShrinkResources = true` for release in `consultme.android.application`; slim `proguard-rules.pro` starter with `-keepattributes SourceFile,LineNumberTable` + `-dontwarn com.google.errorprone.annotations.**` (Hilt 2.60). Validated: `:app:assembleRelease` → ~896 KB unsigned APK.
- **Community docs (#114):** `CONTRIBUTING.md`, `.github/PULL_REQUEST_TEMPLATE.md`, `.github/ISSUE_TEMPLATE/config.yml`.
- **Code of conduct:** `CODE_OF_CONDUCT.md` (Contributor Covenant v2.1); enforcement routes to GitHub's private "Report a vulnerability" flow (no email anywhere).

### Phase 5 — NIA-alignment slice 1 (done, #123)

- New convention plugins: `consultme.android.feature` (composes `library + compose + hilt` + standard feature deps), `consultme.android.room` (KSP + Room + schema export), `consultme.android.test` (`com.android.test`), `consultme.android.lint`, `consultme.jvm.library`.
- **Drop Detekt** — overlaps ~80% with ktlint. Gates are now `spotlessCheck` + `lintRelease` + `test`.
- `enableAggregatingTask = true` was redundant (Hilt 2.55+ default).

### Phase 6 — NIA-alignment slice 2 (module scaffolding, done, #124)

- `:core-designsystem` (owns `ConsultMeTheme` + tokens, moved out of `:app`), `:core-model` (pure-Kotlin), `:core-common` (`Dispatcher` qualifier + `AppDispatchers`), `:core-domain` (pure-Kotlin, depends on `:core-model`). `consultme.android.feature` adds `:core-designsystem` + `:core-ui` as `implementation`.

### Phase 7 — NIA-alignment slice 3 (quality tooling, done, #126)

- **Kover** aggregated coverage (auto-applied by every convention; generated Hilt/Room/Compose code excluded).
- **Module-graph generation** with a Strategy-pattern renderer (`ModuleGraph` → `ModuleGraphRenderer`; `MermaidModuleGraphRenderer` default). Output at `docs/MODULE_GRAPH.md`; CI fails if stale.

### Phase 8 — NIA-alignment slice 4 (baseline profile + macrobenchmark, done, #129)

- `:baselineprofile` (`com.android.test` producer): `BaselineProfileGenerator` + `StartupBenchmarks` (cold-start, two compilation modes).
- `consultme.android.baselineprofile` convention plugin; `:app` consumer wiring (`baselineProfile(projects.baselineprofile)` + `androidx.profileinstaller`).
- Shared GMD via `configureManagedDevices()`. Profile regenerated on demand (`:app:generateReleaseBaselineProfile`); committed `baseline-prof.txt` is canonical.
- **Follow-up (open):** initial baseline-profile bytes need a local device run before forks see startup benefits — noted in `baselineprofile/README.md` (#155).

### Phase 9 — Deferred migrations (done, #132)

- **AGP 8.13.2 → 9.2.1** following the `agp-9-upgrade` skill: built-in Kotlin (removed `kotlin-android`), new AGP DSL (stripped `CommonExtension` type params, `lint.apply {}`, `allDevices.create<ManagedVirtualDevice>`, `tasks.withType<KotlinCompile>`), `gradle.properties` cleanup, library proguard-ref cleanup.
- **Hilt 2.58 → 2.59.2**, **Kover 0.9.1 → 0.9.8**, **`androidx.baselineprofile` → 1.5.0-alpha06** (only line supporting AGP 9).
- Removed the `kotlin-android` plugin alias; lifted the AGP-major + Hilt 2.59+ ignores from `.github/dependabot.yml`.

### Phase 10 — Adopter friction polish (done, #144–#157)

Fixes from a real-world 24-PR downstream port (Java/XML/AppCompat → this template):

- Bootstrap hardening (#145) — NFD-normalize diacritics in `to_pascal()`; broaden slug rename to every `consultme.*`; first pytest suite + Python CI.
- Spotless `kotlinGradle` delimiter fix (#146) — `"/*"` parsed as a regex and silently skipped every `.gradle.kts` header; switched to `plugins \{`.
- `consultme.android.roborazzi` convention plugin (#149) — one-line JVM Compose snapshot testing.
- `configureUnitTests` helper (#154); CodeQL private-repo gate (#150); `:feature-example` placeholder warnings (#147); core-module orientation READMEs (#151); `:baselineprofile` flavor caveat (#155); bootstrap deletes upstream `IMPROVEMENT_PLAN.md` (#153); README adopter sections (#152); `google-services.json` gitignore softening (#148); `createComposeRule` v2 migration (#157); `.gitignore` hygiene (#144, #156).

Pushback accepted (not shipped): auto-bundling `material-icons-extended`, bootstrap deleting `:feature-example`, rewriting `IMPROVEMENT_PLAN.md` to a stub, runtime `TARGET_PACKAGE` derivation.

### Earlier follow-ups (done)

- **`-Xcontext-receivers` → `-Xcontext-parameters`** (#117) — one-line swap in `AndroidExtensions.kt`; unblocked the `org.jetbrains.kotlin.* >=2.3.20` pin.
- **Hilt 2.59.2 → 2.60** (#200) — routine patch; added `-dontwarn com.google.errorprone.annotations.**` for the R8 full-mode `@CanIgnoreReturnValue` missing-class failure.

</details>
