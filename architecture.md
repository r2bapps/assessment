# Architecture

This project is a multi-module Android app that presents user records from one of
two providers: Marvel Database or Random User. Shared behavior lives in common
modules; each provider has its own API contract, response mapping, and data-source
implementation. The `marvel` and `randomuser` product flavors assemble those pieces
into separate app variants.

## Architecture at a glance

```text
Compose screens
      ↕ actions and UI state
ViewModels (:app)
      ↓
Use cases (:domain)
      ↓ repository contract
UserRepository (:domain)
      ↑ implementation
UserRepositoryImpl / RemoteMediator (:data)
      ├── Room cache and local PagingSource (:data)
      └── UserDataSource contract (:data)
              ↑ flavor-specific implementation
         data-marvel or data-randomuser
              ↓
         lib:api-marvel or lib:api-randomuser
```

The app module is the composition root. Hilt binds the domain repository contract
to the shared repository implementation, while the flavor-specific `ApiModule`
provides the matching Retrofit service and `UserDataSource`. Dependencies point
toward domain contracts: domain does not depend on Android UI or data
implementations.

## Modules

| Module | Type | Responsibility |
| --- | --- | --- |
| `:app` | Android application | Compose UI, navigation, ViewModels, app lifecycle, and Hilt composition. Its `marvel` and `randomuser` product flavors add provider-specific API wiring and branding. |
| `:domain` | Kotlin/JVM library | Domain `User` and error models, the `UserRepository` contract, and use cases for listing, filtering, retrieving, and removing users. It stays independent of Android APIs. |
| `:data` | Android library | Shared repository implementation, `UserDataSource` contract, DTO/domain/database mappings, Room database and DAOs, local storage, and Paging `RemoteMediator`. |
| `:data-marvel` | Android library | Marvel-specific `UserDataSource` implementation and mapping from Marvel API responses to shared data models. Included by the `marvel` app flavor. |
| `:data-randomuser` | Android library | Random User-specific `UserDataSource` implementation and mapping from Random User API responses to shared data models. Included by the `randomuser` app flavor. |
| `:lib:api-marvel` | Kotlin/JVM library | Retrofit service, Marvel response/error models, and Moshi adapters for the Marvel API. |
| `:lib:api-randomuser` | Kotlin/JVM library | Retrofit service and response/error models for the Random User API. |
| `:lib:common-api` | Kotlin/JVM library | Shared Retrofit call-adapter types that represent network results as `NetworkResponse`. |
| `:lib:common-test` | Kotlin/JVM library | Reusable test rules and helpers, including coroutine, resource-loading, and MockWebServer support. |
| `:lib:utils` | Kotlin/JVM library | Shared `Either` result type used for success and failure values. |
| `:lib:logger` | Android library | `Logger` abstraction and Timber-backed production implementation. |
| `:lib:tracker` | Kotlin/JVM library | Tracking abstraction; the current delegate is a placeholder and does not yet send analytics. |
| `:lib:image-loader` | Android library | Coil image-loader wrapper with shared memory/disk cache configuration and optional OkHttp client integration. |

### Dependency and flavor boundaries

- `:domain` owns interfaces and business rules. It does not import `:data` or
  provider-specific modules.
- `:data` implements the domain repository and defines the provider-neutral
  `UserDataSource` interface.
- Each `:data-*` module implements that interface and uses its corresponding
  `:lib:api-*` module. Provider pagination formats and response conversion are
  kept behind these implementations.
- `:app` has one `api` flavor dimension. The `marvel` flavor depends on
  `:data-marvel` and `:lib:api-marvel`; `randomuser` depends on
  `:data-randomuser` and `:lib:api-randomuser`. The shared app and data modules
  are used by both.
- `app/src/main` contains shared assets and code. `app/src/marvel` and
  `app/src/randomuser` contain flavor-specific DI and branding resources.
  `app/src/debug` contains preview fixtures and Compose previews.

## Request and persistence flow

1. A Compose screen sends an action to its ViewModel. ViewModels expose screen
   state with `StateFlow`; Paging load state is consumed through Paging Compose.
2. The ViewModel invokes a domain use case, which calls the `UserRepository`
   contract.
3. `UserRepositoryImpl` creates a Room-backed `Pager`. For an unfiltered list,
   its `RemoteMediator` fetches pages through the active flavor's
   `UserDataSource`, then writes users and the next-page key to Room in a
   transaction. Room is the Paging source observed by the UI.
4. A refresh starts from cached Room data while network synchronization runs.
   Network failures are surfaced as Paging errors, so cached rows remain
   available and the UI can offer retry.
5. Search uses the local Room `PagingSource`; it filters the records already
   cached rather than issuing a provider search request. Removing a user is a
   logical delete, retained during later synchronization.

The data layer maps between API DTOs, Room entities, and domain models. This
keeps provider response shapes and storage details out of the UI and use cases.

## Source layout

The modules use conventional Gradle source sets (`src/main`, `src/test`, and,
where applicable, `src/androidTest`). The main packages are organized by role:

```text
app/src/
  main/kotlin/.../
    App.kt, StrictModeDelegate.kt
    di/                 shared Hilt bindings, networking, storage, qualifiers
    ui/
      MainActivity.kt, AppNavigation.kt
      list/             list screen and ViewModel
      detail/           detail screen and ViewModel
      common/           shared image composable and UI extensions
      theme/            Compose theme
  main/res/             shared strings, themes, icons, and drawables
  marvel/               Marvel ApiModule and flavor branding resources
  randomuser/           Random User ApiModule and flavor branding resources
  debug/                Compose previews and preview fixtures

domain/src/main/kotlin/.../domain/
  model/                domain models and errors
  repository/           repository interfaces
  usecase/              application/domain operations
  di/qualifier/          qualified coroutine scope

data/src/main/kotlin/.../data/
  api/                  provider-neutral API data-source contract and DTOs
  storage/              local data-source, Room entities, DAOs, database/migrations
  UserRepositoryImpl.kt, UserRemoteMediator.kt, Mapper.kt

data-marvel/src/main/kotlin/.../data/api/marvel/
data-randomuser/src/main/kotlin/.../data/api/randomuser/
  provider-specific data sources and API-to-shared-model mappers

lib/<module>/src/main/kotlin/
  each small shared library's API or implementation
```

Unit tests and fixtures live beside each module under `src/test`; Android
instrumentation and database tests use `src/androidTest`. The `lib` folder is
for cross-cutting infrastructure and provider API contracts, not feature UI.

## Libraries and build conventions

Dependency versions are centralized in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

| Concern | Main libraries |
| --- | --- |
| UI | Jetpack Compose, Material 3, Activity Compose, Lifecycle Compose |
| Navigation | Navigation Compose with Kotlin serialization for typed routes |
| Dependency injection | Dagger Hilt and AndroidX Hilt integration |
| Data and paging | Room, Paging 3, and Room's Paging integration |
| Networking and JSON | Retrofit, OkHttp (including logging interceptor), Moshi, and the shared Retrofit response adapter |
| Async and state | Kotlin Coroutines, Flow, and `StateFlow` |
| Images and logging | Coil, Timber behind the logger abstraction |
| Tests | JUnit 4, MockK, Turbine, coroutines-test, MockWebServer, Paging/Room testing, and AndroidX test libraries |

Room and Hilt annotation processing use KAPT in Android modules; Moshi code
generation uses KSP in the provider API modules. The project compiles against
Android SDK 36, targets SDK 36, and sets minimum SDK 25. The README documents
the JDK and Gradle wrapper setup.

## UI and system integration

The UI follows unidirectional data flow: screens render state and send actions
to ViewModels. Navigation routes are typed and serializable. `MainActivity`
enables edge-to-edge layout, and screen scaffolds apply safe-drawing insets.
Flavor-specific themes provide their respective colors and branding.

Accessibility work follows WCAG 2.2 AA guidance, including labeled controls,
announced loading state, accessible removal actions, and support for wrapping
text and scrolling details. These are implementation goals, not certified
conformance; verify TalkBack, keyboard navigation, large text, contrast, system
navigation modes, cutouts, and IME behavior on supported devices.

## Building and tests

Build each app variant with:

```shell
./gradlew assembleMarvelDebug
./gradlew assembleRandomuserDebug
```

Run unit tests for both debug variants with:

```shell
./gradlew testMarvelDebugUnitTest testRandomuserDebugUnitTest
```
