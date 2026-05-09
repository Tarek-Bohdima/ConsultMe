# Contributing to ConsultMe

Thanks for taking an interest in this template. ConsultMe is a Jetpack Compose
Android template — its scope is the build infrastructure and convention plugins,
not feature code (the `:feature-example` module is intentionally a placeholder
that adopters replace). PRs that improve the template's ergonomics, tooling,
or documentation are very welcome; PRs that add product features should usually
land in a fork instead.

## Code of conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md).
By participating you agree to uphold it.

## Before you start

1. Open an issue for non-trivial changes (anything beyond a doc/typo fix or a
   one-line bump). The roadmap lives in [`docs/IMPROVEMENT_PLAN.md`](docs/IMPROVEMENT_PLAN.md);
   if your idea fits an existing phase, link it. If it doesn't, the issue is the
   right place to discuss whether it should.
2. If you're touching one of the migrations that has its own dedicated PR cadence (now: just AGP 10 when it lands; previously AGP 9, Hilt 2.59+,
   Kotlin 2.3.20+), read the "Phase 5" section of `docs/IMPROVEMENT_PLAN.md`
   first — those bumps are pinned in `.github/dependabot.yml` for reasons that
   are documented there. Each is its own dedicated PR, not a passive bump.

## Local setup

You'll need:

- **JDK 17** (the Gradle toolchain pulls it via `jvmToolchain(17)`, but having
  it on `JAVA_HOME` keeps Android Studio happy).
- **Android Studio** (any version that supports AGP 9.2). The project opens
  cleanly with no manual configuration.
- **Android SDK with API 26+** for `minSdk` and API 36 for `compileSdk` /
  `targetSdk`. AGP will prompt to install missing components on first sync.

## The local CI loop

CI runs these in order. Run the same gates locally before opening a PR:

```bash
./gradlew spotlessCheck     # formatting + license headers
./gradlew lintRelease       # Android Lint, release variant
./gradlew test              # all unit tests
./gradlew koverHtmlReport   # aggregated coverage at build/reports/kover/html/
./gradlew moduleGraph       # regenerate docs/MODULE_GRAPH.md if module deps changed
./gradlew :app:assembleRelease  # exercises R8 + resource shrinking
./gradlew connectedAndroidTest  # instrumented tests (needs device/emulator)
```

CI runs `moduleGraph` and fails the build if `docs/MODULE_GRAPH.md` is stale relative to your changes — regenerate and commit it whenever you add or remove an inter-module dependency.

If `spotlessCheck` fails, `./gradlew spotlessApply` autofixes it. If
`lintRelease` flags something inherent to the variant (rare), regenerate the
baseline for that module with `./gradlew :<module>:updateLintBaseline` rather
than hand-editing the XML.

## Pull request conventions

- **`main` is protected.** Direct pushes are rejected; every change goes
  through a PR. `build_and_test` is a required check.
- **Conventional commit prefixes.** The repo's commit log uses `feat:`,
  `fix:`, `chore:`, `docs:`, `ci:`, `build:`, `test:`, and `refactor:`. Match
  the surrounding style — a glance at `git log --oneline -20` shows the cadence.
- **One scope per PR.** Splitting CI changes from R8 changes from doc changes
  keeps each diff easy to review and roll back independently. The roadmap
  documents how a phase decomposes.
- **PR description.** Use the template (it'll prefill when you open the PR).
  The "Test plan" section is load-bearing — list what you ran locally and what
  CI will validate. Reviewers often scan the test plan before the diff.
- **Keep dep bumps to Dependabot.** It runs weekly and groups updates
  sensibly. Manual bumps inside a feature PR muddy the diff and bypass the
  ignore rules in `.github/dependabot.yml`.

## License header

Every `.kt` and `.gradle.kts` file needs the Spotless-injected header:

```
// Copyright $YEAR MyCompany
```

`spotlessApply` writes the year and the `template.company` value from
`gradle.properties` (defaults to `MyCompany` for the upstream template).
Adopters override `template.company` once per fork; you don't need to change
it when contributing back upstream.

## Where things live

- `:app` — application module, wires Hilt + Compose root + nav.
- `:feature-*` — screen-level features (currently just `:feature-example`).
- `:core-ui`, `:core-data`, `:core-database`, `:core-testing` — shared layers.
- `build-logic/` — convention plugins. Module build scripts compose these
  instead of redeclaring AGP/Kotlin/lint config. Available:
  `consultme.android.application` / `library` / `compose` / `hilt` /
  `feature` / `room` / `test` / `lint`, plus `consultme.jvm.library`.
  Shared helpers live in
  `build-logic/convention/src/main/kotlin/com/thecompany/consultme/buildlogic/`.
- `gradle/libs.versions.toml` — single source of truth for versions, libraries,
  bundles, and plugins.
- `docs/IMPROVEMENT_PLAN.md` — the roadmap. Check it before non-trivial work.
- `CLAUDE.md` — orientation for AI coding assistants; also a good human read for
  the conventions enforced by tooling.

## Reporting bugs and security issues

- **Bugs** — use the [bug report issue template](.github/ISSUE_TEMPLATE/bug_report.md).
- **Security issues** — please don't open a public issue. Use the **Report a
  vulnerability** option in the repository's Security tab; that opens a private
  channel visible only to the maintainer. See [`SECURITY.md`](SECURITY.md) for
  what to expect after submitting.

Thanks again — every PR that makes the template easier to fork helps the next
adopter.
