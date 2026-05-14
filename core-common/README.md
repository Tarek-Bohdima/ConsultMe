# :core-common

Pure-Kotlin shared utilities consumed across the module graph. No
Android dependencies, no Hilt, no AGP. Built with the
`consultme.jvm.library` convention.

## What's here today

- `AppDispatchers` — enum of `Main` / `IO` / `Default` dispatchers,
  the NIA-style qualifier for Hilt-injected coroutine contexts.
- `Dispatcher` — the matching `@Qualifier` annotation for parameter
  injection.

Adopters typically inject like:

```kotlin
class Foo @Inject constructor(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
)
```

The wiring lives in `:app`'s Hilt module (or a per-feature module if
preferred).

## What else belongs here

- Pure-Kotlin extension functions used by ≥ 2 modules
- Result wrappers / sealed error hierarchies that are framework-agnostic
- Time providers, ID generators, encoding helpers

## What does **not** belong here

- Android types (`Context`, `Intent`, anything from `androidx.*`) —
  those belong in `:core-ui` or a feature module.
- Room / data-layer types — those live in `:core-data` /
  `:core-database`.
- Domain use cases — those live in `:core-domain`.

## Why a separate module

Pure-Kotlin means JVM tests run with no Android shim, and downstream
modules pay no AGP-config cost when they only need a dispatcher
qualifier. See `docs/MODULE_GRAPH.md` for the broader module graph.
