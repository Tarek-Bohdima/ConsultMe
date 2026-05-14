# :core-domain

Pure-Kotlin domain layer — use-case classes that orchestrate repository
calls and expose a single `operator fun invoke(...)` per use case. No
Android dependencies, no Hilt, no AGP. Built with the
`consultme.jvm.library` convention.

## What belongs here

- Use-case classes (`GetUserPreferencesUseCase`, `SyncFridgeUseCase`, …)
- Domain-level errors and result wrappers, if they're independent of
  the data layer

## What does **not** belong here

- Composables, view models, or any Android types — those live in
  `:feature-*` or `:core-ui`.
- Room entities, DAOs, or DTOs — those stay in `:core-data` /
  `:core-database`. Map them to plain `:core-model` types at the
  repository boundary.
- Coroutine dispatcher injection at construction time — use the
  `Dispatcher` / `AppDispatchers` qualifiers from `:core-common`
  instead.

## Why a separate module

Keeping use cases pure-Kotlin means they run in JVM unit tests with no
Robolectric / AndroidJUnitRunner overhead, and they're trivially
swappable across `:feature-*` modules without dragging Android types
through compile classpaths.

The module ships empty; adopters add use-case files alongside this
README. See `docs/MODULE_GRAPH.md` for how it relates to the rest of
the module graph.
