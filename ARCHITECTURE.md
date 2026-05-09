# Architecture

ConsultMe follows a **layered, multi-module** architecture aligned with Google's [`android/nowinandroid`](https://github.com/android/nowinandroid). Each layer has a clear seam, and modules communicate only through public APIs.

## Layers

```mermaid
graph TD
    subgraph Presentation
        APP[":app"]
        FEAT[":feature-*"]
        DS[":core-designsystem"]
        UI[":core-ui"]
    end
    subgraph Domain
        DOM[":core-domain"]
        MOD[":core-model"]
    end
    subgraph Data
        DAT[":core-data"]
        DB[":core-database"]
    end
    subgraph Cross-cutting
        COM[":core-common"]
        TST[":core-testing"]
    end

    APP --> FEAT
    APP --> DS
    APP --> UI
    FEAT --> DS
    FEAT --> UI
    FEAT --> DOM
    DOM --> MOD
    DAT --> DB
    DAT --> MOD
```

The Presentation layer is Android. The Domain layer and `:core-common` are **pure-Kotlin** modules (`consultme.jvm.library`) — no Android SDK on the classpath, so framework imports won't compile there. The Data layer is Android (Room).

`./gradlew moduleGraph` regenerates [`docs/MODULE_GRAPH.md`](docs/MODULE_GRAPH.md) — that file reflects the actual dep tree, including the `:baselineprofile` producer→`:app` consumer relationship.

## Module responsibilities

| Module | Layer | Belongs here | Doesn't belong |
|---|---|---|---|
| `:app` | Presentation root | `Application` class, navigation graph, top-level wiring, Hilt entry point | Screen logic — push to `:feature-*` |
| `:feature-*` | Presentation | Screens, ViewModels, screen-specific state and side-effects | Cross-feature shared composables (use `:core-ui`) |
| `:core-designsystem` | Presentation | Theme (`ConsultMeTheme`), color/typography/icon registries | Stateful composables, business logic |
| `:core-ui` | Presentation | Cross-feature stateless composables (loading, empty, error states, etc.) | Domain types, business logic |
| `:core-domain` | Domain (pure Kotlin) | Use-cases (`Get*UseCase`, `Update*UseCase`, etc.) | Android types, framework dependencies |
| `:core-model` | Domain (pure Kotlin) | Pure data classes / sealed types | Logic, side-effects, persistence concerns |
| `:core-data` | Data | Repositories, DTO ↔ domain mappers, network/database orchestration | Database tables (those are in `:core-database`) |
| `:core-database` | Data | Room entities, DAOs, database class | Repository APIs (those live in `:core-data`) |
| `:core-common` | Cross-cutting (pure Kotlin) | Dispatchers, qualifiers, generic utilities | Anything Android-specific |
| `:core-testing` | Cross-cutting | Test fixtures, dispatcher rule, Hilt test runner | Production code |
| `:baselineprofile` | Build-only producer | `BaselineProfileGenerator`, `StartupBenchmarks` | Anything that ships in `:app`'s release APK except `baseline-prof.txt` |

## Data flow (UDF)

A typical interaction — user taps a button, screen reflects new state:

```mermaid
sequenceDiagram
    participant User
    participant Screen as Screen (@Composable)
    participant VM as ViewModel
    participant UC as UseCase
    participant Repo as Repository
    participant DS as DataSource (Room)

    User->>Screen: tap button
    Screen->>VM: onEvent()
    VM->>UC: invoke(...)
    UC->>Repo: getX(...)
    Repo->>DS: query(...)
    DS-->>Repo: Flow<Entity>
    Repo-->>UC: Flow<DomainModel>
    UC-->>VM: Flow<DomainModel>
    VM->>VM: update _uiState (StateFlow)
    VM-->>Screen: collectAsStateWithLifecycle re-emits
    Screen->>User: recompose with new state
```

State is **unidirectional**:

- **Events flow down** (`Screen → VM → UseCase → Repo → DataSource`).
- **State flows up** (`Repo → UseCase → VM → Screen`) as `Flow<T>`, lifted to `StateFlow<T>` at the VM boundary so Compose can collect with lifecycle awareness.

The `:feature-example` module ships a worked example of this pattern at minimal scope: `ExampleViewModel` exposes a `StateFlow<ExampleUiState>` and `onClicked()`, and `ExampleScreen` collects via `collectAsStateWithLifecycle()`.

## Stateless / stateful screen split

Each feature exposes **two overloads**:

```kotlin
// Stateful — used by the host activity / nav graph. Pulls a hilt-managed ViewModel.
@Composable
fun ExampleScreen(modifier: Modifier = Modifier, viewModel: ExampleViewModel = hiltViewModel())

// Stateless — used by `@Preview` and Compose UI tests. No ViewModel coupling.
@Composable
fun ExampleScreen(uiState: ExampleUiState, onClick: () -> Unit, modifier: Modifier = Modifier)
```

Why both:

- **Previews** can render every UI state without a Hilt graph — pass `ExampleUiState.Clicked`, see what it looks like.
- **UI tests** of the stateless overload (`ExampleScreenTest`) are a one-liner: drive state via the parameter, assert what's on screen.
- **Stateful tests** (`ExampleScreenStatefulTest`) construct a real `ExampleViewModel` instance and pass it as `viewModel = ...`, exercising the full screen↔VM round-trip without needing Hilt.

## Navigation

Navigation is owned by `:app` — it's the only module that knows about every feature. Each feature exposes a public composable entry point and a route:

```kotlin
// :feature-example
@Composable fun ExampleScreen(...)
fun NavGraphBuilder.exampleScreen() { composable<ExampleRoute> { ExampleScreen() } }

// :app
NavHost(...) {
    exampleScreen()
    // otherFeatureScreen()
}
```

Adopters add destinations the same way. **Never reach into a feature's internal composables** from `:app` or another feature — go through the public route extension.

## Why pure-Kotlin domain layers

`:core-model`, `:core-domain`, and `:core-common` use `consultme.jvm.library`, not `android.library`. Trade-off:

- **Build speed**: 5–10× faster compilation (no AGP, no resource processing, no R class).
- **Discipline**: Android SDK isn't on the classpath, so `import android.*` won't compile. Forces clean separation of concerns.
- **Test speed**: pure JUnit, no Robolectric or instrumentation needed.

The cost: Hilt `@Provides` modules (which reference `dagger.hilt.components.SingletonComponent`) live in `:app` or `:core-data`, not in `:core-common`. The qualifier *annotations* (`@Dispatcher(IO)`) live in `:core-common`; the *bindings* live next to the consumer.

## Cross-cutting conventions

- **Dispatchers**: `@Dispatcher(AppDispatchers.IO)` qualifier from `:core-common`. Adopters provide the binding (`@Provides @Dispatcher(IO) fun providesIo() = Dispatchers.IO`) wherever they apply Hilt.
- **State**: `Flow<T>` for streams, `Result<T>` for one-shot operations. Domain layer surfaces `Flow<DomainModel>` / `Result<DomainModel>`, never raw DTOs or `Response<T>`.
- **Theme**: feature modules pull `:core-designsystem` automatically via the `consultme.android.feature` convention plugin — don't redeclare.
- **Coverage**: Kover instruments every module that applies an `consultme.android.*` or `consultme.jvm.library` convention. Generated Hilt/Room/Compose code is excluded in `consultme.kover.gradle.kts` so the metric reflects real coverage.
