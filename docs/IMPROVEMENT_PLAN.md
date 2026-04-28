# Template improvement plan

ConsultMe is a Jetpack Compose multi-module Android template. This document is the living roadmap for getting it from "works for me" to "drop-in flexible template that any team can fork in five minutes." Phases are independently shippable; pick them off in any order, but the suggested sequence delivers the most value per PR.

## Status at a glance

| Phase | Theme | State |
|---|---|---|
| 0 | Cleanup & flexibility | **In progress** (this PR) |
| 1 | Convention plugins (`build-logic/`) | Not started |
| 2 | Template ergonomics (bootstrap script, parameterized header) | Not started |
| 3 | Real example tests | Not started |
| 4 | Production-readiness (R8, CI artifacts, instrumented tests) | Not started |
| 5 | Deferred migrations (AGP 9, Hilt 2.59+, Kotlin 2.3.20) | Blocked by upstream pin in `dependabot.yml` |

Tick the table when phases land. Each phase below lists scope, rationale, and a rough size; sub-bullets are the concrete deltas.

---

## Phase 0 — Cleanup & flexibility (this PR)

Goal: strip migration-era cruft and make the placeholder feature module agnostic so the template doesn't ship as "ChatApp."

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

## Phase 1 — Convention plugins

Goal: eliminate the ~70 lines of identical Gradle config in every library module so that adding a feature is a one-liner.

Create a `build-logic/` included build with precompiled script plugins:
- `consultme.android.application` — applies AGP-app + kotlin + ksp + hilt + compose-compiler; sets compileSdk/minSdk/JVM 17/buildFeatures defaults/lint.
- `consultme.android.library` — same set minus app concerns; flag for `withCompose`.
- `consultme.android.feature` — depends on `library` + Compose; auto-includes `:core-ui` and `:core-testing`.
- `consultme.android.hilt` — applied where DI is wanted.

After this, a feature module's build script collapses to:
```kotlin
plugins { id("consultme.android.feature") }
android { namespace = "com.thecompany.consultme.feature.foo" }
```

Notes for whoever picks this up:
- Use precompiled script plugins (`build-logic/convention/src/main/kotlin/*.gradle.kts`), not `buildSrc`. `buildSrc` invalidates the entire build cache on edit; an included build doesn't.
- Reference `gradle/libs.versions.toml` from the convention plugins via the version catalog accessor (`libs` is available).
- Resist the urge to over-parameterize. Two or three plugins beat one mega-plugin with twelve flags.

## Phase 2 — Template ergonomics

Goal: turn the manual "find/replace these 8 places" rename ritual into one command and parameterize the company name.

- **`scripts/rename-template.sh com.acme.myapp "My App Name"`** that does the package rename, namespace updates, `applicationId` change, manifest activity references, `app_name`, and `rootProject.name` in one pass. Cross-shell-safe (use POSIX `sh` or python).
- **Parameterize the Spotless license header** via `gradle.properties` (`template.company=MyCompany`, `template.licenseYear=$YEAR`). Read in the root `build.gradle.kts` Spotless config.
- **Rewrite README's "How to Rename" section** to point at the script as the primary path, keep the manual fallback as an appendix. Add new sections: "How to add a new feature module" (after Phase 1, this is one line), "How to write a Hilt-aware test" (snippet using `:core-testing`'s `HiltTestRunner`), "How to regenerate lint baselines" (`./gradlew :module:updateLintBaseline`).

## Phase 3 — Real example tests

Goal: replace the empty `ExampleUnitTest` shells with code that doubles as documentation for "how to test in this template."

- `:core-testing` exposes a small `HiltTestRule` helper composing `HiltAndroidRule` + `MainDispatcherRule`.
- `:feature-example` ships a real `ExampleViewModel` exposing a `StateFlow`, plus:
  - A unit test using Turbine to assert flow emissions.
  - A Hilt-injected test using `core-testing`'s runner.
  - A Compose UI test asserting the screen renders.
- Each test stays small (≤ 30 lines) — the goal is exemplification, not coverage.

## Phase 4 — Production-readiness

Goal: a fork from this template should be one signing config away from a Play release.

- Flip `isMinifyEnabled = true` for release in `:app`. Add starter `proguard-rules.pro` with the Hilt + Compose + Room rules every project needs (most are already in the AGP defaults; document the few that aren't).
- CI additions in `.github/workflows/android_ci.yml`:
  - `./gradlew assembleRelease` (catches APK build regressions that `test` misses).
  - Upload `*/build/reports/lint-results-*.html` and `*/build/reports/tests/**` as artifacts.
  - Add a Gradle Managed Devices step for `connectedCheck` (cheaper and more reliable than self-hosted emulators).
- Add `CONTRIBUTING.md`, `PULL_REQUEST_TEMPLATE.md`, and a polished `CODE_OF_CONDUCT.md` (Contributor Covenant).

## Phase 5 — Deferred migrations

Currently silenced in `.github/dependabot.yml`. Each is its own dedicated PR, not a passive bot bump:

- **AGP 8.x → 9.x.** Drops the `kotlin-android` plugin requirement and forces Gradle ≥ 9.4. Touches every module's plugin block. Requires removing `alias(libs.plugins.kotlin.android)` and verifying Compose/KSP/Hilt all play nicely with AGP 9's bundled Kotlin support.
- **Hilt 2.59+.** Hard-requires AGP 9; do this in the same PR or a follow-up to the AGP 9 migration.
- **Kotlin 2.3.20.** Currently produces compilation errors in `:core-testing` and `:feature-example`. Investigate whether it's a kapt/KSP processor mismatch or a real source incompatibility before unpinning.

## Known follow-ups not yet phased

- **`-Xcontext-receivers` is deprecated** (every module sets this in `freeCompilerArgs`). Kotlin compiler warns: replace with `-Xcontext-parameters` and migrate to the new syntax. Will become an error. Worth a one-shot PR — the migration is mechanical, but worth verifying nothing in Compose/Hilt-generated code uses receivers.
- **KSP `2.3.4` lags Kotlin `2.3.10`.** Dependabot will likely propose a bump on the next run; let it.

## Quality bets to consider (no phase yet)

These are valuable but don't fit cleanly into the above. Worth deciding on before Phase 4:

- **Kover** for coverage with HTML/XML reports. Add to `:core-testing` consumers.
- **Baseline profiles + macrobenchmark module** for startup performance.
- **`dependency-analysis-android-gradle-plugin`** to flag unused/misplaced dependencies — high signal for multi-module hygiene.
- **Gradle build scans** opt-in via `develocity { server = ... }` if there's a Develocity instance available.

## How to use this document

When starting a phase, change its row in the status table to **In progress**, link the PR, and update sub-bullets with checkboxes if it helps. When merged, mark **Done** with the PR number. The point is to keep the next-best action obvious to whoever opens the repo a month from now.
