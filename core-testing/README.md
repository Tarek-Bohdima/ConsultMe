# :core-testing

Test fixtures shared across every module. Re-exports the standard
testing toolbox (JUnit + Truth + Turbine + MockK + Hilt-testing +
coroutines-test + Espresso) and ships the `HiltTestRunner` that every
module uses as its `testInstrumentationRunner`. Built with the
`consultme.android.library` convention.

## What belongs here

- Test infrastructure shared by two or more modules: rules, runners,
  custom matchers, fake/stub builders that aren't feature-specific.
- The current set:
  - **`HiltTestRunner`** — `AndroidJUnitRunner` subclass that swaps in
    `HiltTestApplication`. Wired automatically via every Android
    convention (`testInstrumentationRunner = "...HiltTestRunner"`).
  - **`HiltTestRule`** — composed rule pairing `HiltAndroidRule` with
    `MainDispatcherRule` so a `@HiltAndroidTest` only needs one
    `@get:Rule`.
  - **`MainDispatcherRule`** — swaps `Dispatchers.Main` for a
    `TestDispatcher` for the duration of the test. Use whenever code
    under test dispatches to `Dispatchers.Main` (typically ViewModels
    backed by `viewModelScope`).

## What does **not** belong here

- Feature-specific fakes — those live next to the feature they fake
  (`:feature-example/src/test/...`).
- Production-side test scaffolding (DI test modules, fake repositories
  exposed at build time) — those live in the owning module's
  `src/testFixtures/` or `src/debug/` source set.

## How modules consume it

Modules pull in the bundle via the conventions' standard
`testImplementation` + `androidTestImplementation` wiring — **do not**
add JUnit / Espresso / MockK directly in module build scripts.

```kotlin
dependencies {
    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))
}
```

The re-export uses Gradle `api(...)` (see `core-testing/build.gradle.kts`),
backed by the `test-shared` bundle in `gradle/libs.versions.toml`. New
shared testing libs go into that bundle, not into per-module build
scripts.

## Why a separate module

A single owning module for the testing toolbox means version bumps
(JUnit, Truth, MockK, …) happen in one place, and adopters renaming
the template don't have to chase per-module test deps. Google's
`testing/testing-setup` Claude Code skill is the companion playbook
when extending the pattern.

See `docs/MODULE_GRAPH.md` for how it relates to the rest of the
module graph.
