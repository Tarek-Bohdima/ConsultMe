# ConsultMe

[![Android CI](https://github.com/Tarek-Bohdima/ConsultMe/actions/workflows/android_ci.yml/badge.svg)](https://github.com/Tarek-Bohdima/ConsultMe/actions/workflows/android_ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Platform](https://img.shields.io/badge/platform-android-green.svg)
![Min API](https://img.shields.io/badge/Min%20API-26-purple)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)


[![GitHub stars](https://img.shields.io/github/stars/Tarek-Bohdima/ConsultMe)](https://github.com/Tarek-Bohdima/ConsultMe/stargazers) [![GitHub forks](https://img.shields.io/github/forks/Tarek-Bohdima/ConsultMe)](https://github.com/Tarek-Bohdima/ConsultMe/network)

**ConsultMe** is a template project for Jetpack Compose applications, featuring integrated tools for code quality and automation. It includes:

- **Spotless + ktlint:** Automated code formatting and license-header enforcement
- **Android Lint:** Kotlin and Compose correctness checks (release variant, fail-on-error)
- **Kover:** Aggregated test coverage with HTML + XML reports
- **Module-graph generation:** `./gradlew moduleGraph` keeps `docs/MODULE_GRAPH.md` in sync with the module dep tree
- **Baseline profile pipeline:** `:baselineprofile` macrobenchmark module generates a startup profile shipped with the release APK (~15–30% cold-start win)

## Features

- Fully configured for Jetpack Compose and a multi-module architecture.
- Code quality tools included and pre-configured.
- 100% Kotlin codebase, using Coroutines and Flow.
- Dependency injection with Hilt.

## Getting Started

Do not clone this repository directly. The recommended way to use this template is to create your own repository from it.

1.  Click the **Use this template** button on the main repository page and select **Create a new repository**.
2.  Give your new project a name and description. This creates a completely new and independent repository.
3.  Clone your new repository to your local machine and open it in Android Studio.
4.  Follow the instructions in the **"How to Rename and Refactor"** section below to customize it for your project.

> **Note on Forking:** If your intention is to contribute changes back to this template, you should fork the repository instead.

## How to Rename and Refactor

After creating your new repository, run the bootstrap script — it does the package, namespace, `applicationId`, project name, theme/application class, manifest, and `app_name` rewrites in one pass:

```bash
python3 scripts/rename-template.py com.acme.myapp "My App Name"
```

The first argument is the new package (also used as `applicationId`). The second is the user-facing app name; its PascalCase form (`MyAppName`) becomes `rootProject.name`, the theme name, and the `Application` class name. The four convention plugin IDs under `build-logic/` are also rewritten (`consultme.android.*` → `myappname.android.*`). Re-running with the same arguments is a no-op.

After the script completes, finish the bootstrap by hand:

1. **License header company name:** open `gradle.properties` and set `template.company` (consumed by the root `build.gradle.kts` Spotless config). Then run `./gradlew spotlessApply` to rewrite every header.
2. **License file:** open `LICENSE.md` and replace `[year]` and the placeholder name with your own.
3. **README and docs:** update the badges (CI, stars, forks) to point at your repo, and replace the project description in this file. The script intentionally skips `*.md` so it doesn't break upstream-template links.
4. **Feature module:** replace the placeholder content in `:feature-example` (start with `ExampleScreen.kt`), and rename the module (`:feature-example` → `:feature-yourname`) once you know what you're building.
5. **Remove template funding file:** delete `.github/FUNDING.yml`, or replace it with your own sponsorship info.

If you'd rather rename by hand, expand the manual fallback below.

<details>
<summary>Manual rename fallback</summary>

Use Android Studio's **Refactor > Rename** for the package step.

1. **Project name:** in `settings.gradle.kts`, change `rootProject.name`.
2. **Application ID & namespaces:** in `app/build.gradle.kts` and every library module's `build.gradle.kts`, change `namespace` (and `applicationId` in `:app`) from `com.thecompany.consultme` to your new ID.
3. **Package name:** rename the `com.thecompany.consultme` package via Android Studio refactor — that handles source file moves, package declarations, and imports.
4. **Theme + application class:** rename `ConsultMeTheme`, `Theme.ConsultMe` (in `app/src/main/res/values/themes.xml`), and `ConsultMeApplication` (class + filename + `AndroidManifest.xml` reference) to match your new project name.
5. **App display name:** in `app/src/main/res/values/strings.xml`, change `app_name`.
6. **Convention plugin IDs:** rename the four files under `build-logic/convention/src/main/kotlin/consultme.android.*.gradle.kts` and update every `id("consultme.android.*")` reference in module build scripts.
7. Then continue with the post-script steps above (license header, LICENSE file, README badges, feature module, FUNDING.yml).

</details>

## How to add a new feature module

The `consultme.android.feature` convention plugin makes a new feature module a one-liner. Create `feature-<name>/build.gradle.kts`:

```kotlin
plugins {
    id("consultme.android.feature")
}

android {
    namespace = "com.thecompany.consultme.feature.<name>"
}
```

Add `include(":feature-<name>")` to `settings.gradle.kts` and depend on it from `:app` via `implementation(projects.feature<NameInPascalCase>)`. The `feature` convention composes `library + compose + hilt` and pulls in the standard feature deps: lifecycle-runtime-compose, lifecycle-viewmodel-compose, hilt-navigation-compose, `:core-designsystem`, `:core-ui`, and `:core-testing` for unit + instrumented tests.

### Module layout

The template ships these modules (NIA-aligned):

- `:app` — application module, Compose root + nav.
- `:feature-example` — placeholder feature module; replace with your own and rename.
- `:core-designsystem` — Compose theme (`ConsultMeTheme`), color/typography tokens.
- `:core-ui` — shared Compose composables (loading/empty/error states). Scaffold.
- `:core-model` — pure-Kotlin data classes (no Android). Scaffold.
- `:core-common` — pure-Kotlin shared utilities; ships the `Dispatcher` qualifier + `AppDispatchers` enum.
- `:core-domain` — pure-Kotlin use-cases; depends on `:core-model`. Scaffold.
- `:core-data` — repository layer.
- `:core-database` — Room database (uses `consultme.android.room`).
- `:core-testing` — re-exports JUnit/Truth/Turbine/MockK/Hilt-testing/Espresso via `api(...)`, plus `HiltTestRunner`.
- `:baselineprofile` — macrobenchmark + baseline-profile generator that ships the profile with `:app`'s release APK. See `docs/MODULE_GRAPH.md` for the producer→consumer wiring.

### Other available convention plugins

- `consultme.android.application` — the `:app` module.
- `consultme.android.library` — generic Android library (no Compose).
- `consultme.android.compose` — adds Compose BOM, ui/material3 deps, enables `buildFeatures.compose`.
- `consultme.android.hilt` — Hilt + KSP wiring.
- `consultme.android.feature` — `library + compose + hilt + standard feature deps + :core-testing`.
- `consultme.android.room` — KSP + Room runtime/ktx/compiler + schema export dir.
- `consultme.android.test` — `com.android.test`, for benchmark/macrobenchmark modules.
- `consultme.android.baselineprofile` — `consultme.android.test` + `androidx.baselineprofile` + macro/uiautomator deps; used by `:baselineprofile`.
- `consultme.android.lint` — pure-Kotlin module that contributes custom Lint checks.
- `consultme.jvm.library` — pure-Kotlin module (no AGP), e.g. for `:core-model` / `:core-domain`.
- `consultme.kover` — coverage instrumentation (auto-applied by every Android/JVM convention; opt out by removing the line).
- `consultme.modulegraph` — root-only; registers `:moduleGraph` to emit `docs/MODULE_GRAPH.md`.

## How to write a Hilt-aware test

Every module declares `:core-testing` for both unit and instrumented tests, so JUnit/Turbine/MockK/Hilt-testing/Espresso are already on the classpath:

```kotlin
testImplementation(projects.coreTesting)
androidTestImplementation(projects.coreTesting)
```

For an instrumented test that needs Hilt injection, annotate with `@HiltAndroidTest` and use the runner that the convention plugins already wire in (`com.thecompany.consultme.core.testing.HiltTestRunner`):

```kotlin
@HiltAndroidTest
class MyFeatureTest {
    @get:Rule val hilt = HiltAndroidRule(this)

    @Before fun setUp() { hilt.inject() }

    @Test fun feature_does_something() { /* ... */ }
}
```

No need to redeclare JUnit/Hilt-testing dependencies in the module's `build.gradle.kts` — `:core-testing` re-exports them with `api(...)`.

## How to regenerate lint baselines

Each module ships its own `lint-baseline.xml`. Regenerate after adding code that introduces new lint warnings (rather than hand-editing):

```bash
./gradlew :feature-example:updateLintBaseline
```

Replace `:feature-example` with the module you're updating. CI runs `lintRelease` and fails on any non-baselined violation.

## Code Quality

- **Spotless + ktlint**: Consistent formatting and license-header enforcement on every `.kt` / `.gradle.kts` file.
- **Android Lint**: Kotlin and Compose correctness checks; CI runs `lintRelease` and fails on any non-baselined violation.
- **Kover**: Aggregated test coverage. `./gradlew koverHtmlReport` produces a project-wide report under `build/reports/kover/html/`. Generated Hilt/Room/Compose code is excluded from instrumentation in `consultme.kover.gradle.kts`.
- **Module graph**: `docs/MODULE_GRAPH.md` is regenerated by `./gradlew moduleGraph`. The renderer is pluggable (Strategy pattern via `ModuleGraphRenderer`); ships with a Mermaid implementation. CI fails if the committed graph is stale.
- **Baseline profile**: `:baselineprofile` is a producer macrobenchmark module that emits `app/src/main/baseline-prof.txt`. The committed profile is consumed by `androidx.profileinstaller` at install time and gives a measurable cold-start win. Regenerate via `./gradlew :app:generateReleaseBaselineProfile` (uses the existing `pixel6api30` GMD).

## Versioning

Tags follow SemVer with a template-adopter lens: **MAJOR** = breaking change for downstream forks (`minSdk` bump, AGP/Kotlin major migration, convention-plugin API rename), **MINOR** = a phase landing or new opt-in tooling, **PATCH** = bug fixes and dep bumps. Tags align with phase boundaries in [`docs/IMPROVEMENT_PLAN.md`](docs/IMPROVEMENT_PLAN.md), and every tag ships as a GitHub Release. See [`CLAUDE.md`](CLAUDE.md#versioning-and-tags) for the full policy.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
