# :feature-example

> **Placeholder.** Replace this module with your real feature(s) before shipping.

This module exists to give new adopters a worked example of how a
`consultme.android.feature`-conventioned screen module is wired:

- One Composable per screen (stateful + stateless overloads)
- One `ViewModel` exposing a `StateFlow<UiState>`
- Unit tests in `src/test/`, Compose UI tests in `src/androidTest/`
- Hilt-injected smoke test under `src/androidTest/` showing the runner+rule wiring

Use it as a reference when you build your first real feature module.
**Once you have one, delete this module** (and remove the
`projects.featureExample` line from `app/build.gradle.kts`) — the launcher
activity in `:app` currently renders `ExampleScreen` from here, so leaving the
module around means your app ships a placeholder screen.

## How to remove this module

1. Add your replacement feature module (e.g. `:feature-home`) and wire it into
   `:app` via `implementation(projects.featureHome)`.
2. Update `MainActivity` to navigate to your new screen instead of `ExampleScreen`.
3. Delete the `implementation(projects.featureExample)` line in `app/build.gradle.kts`.
4. Remove `include(":feature-example")` from `settings.gradle.kts`.
5. `rm -rf feature-example/`.

See `docs/MODULE_GRAPH.md` and the CLAUDE.md "Module graph" section for the
broader module-graph conventions this template enforces.
