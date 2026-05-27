# Compose NewsReader — Claude Code Project Instructions

## Project Overview

Multi-module Android news reader app demonstrating Modern Android Development:
Jetpack Compose + Material 3, Hilt DI, Room (offline-first), Retrofit + OkHttp,
Kotlin Coroutines/Flow, kotlinx.serialization, Coil 3, DataStore preferences.

Target: compileSdk 35, minSdk 26. JDK 17. Kotlin 2.1.21. AGP 8.9.1.

Requires a NewsAPI key in `local.properties`:
```
NEWS_API_KEY=your_api_key_here
```

## Build Commands

```bash
./gradlew assembleDebug           # Build debug APK
./gradlew testDebugUnitTest       # Run all unit tests
./gradlew lintDebug               # Run Android lint
./gradlew :module:path:test       # Run tests for a single module
```

## Architecture

### Layers (top to bottom)
```
:app
  └── :feature:home  :feature:detail  :feature:bookmarks  :feature:settings
        └── :core:ui  :core:domain  :core:common
              └── :core:data
                    ├── :core:network
                    └── :core:database
                          └── :core:model
```

### Module Dependency Rules

- Feature modules depend only on `:core:*` modules — **never on each other**
- `:core:model` is pure Kotlin/JVM (no Android framework)
- `:core:domain` is pure Kotlin/JVM (no Android framework)
- `:core:model` provides `Result<T>` (sealed interface: `Success`, `Error`, `Loading`)
- `:core:common` provides `@IoDispatcher`, `@DefaultDispatcher`
- `:core:data` depends on `:core:network`, `:core:database`, `:core:model`, `:core:common`
- `:core:domain` owns repository interfaces (`ArticleRepository`, `UserPreferencesRepository`);
  `:core:data` provides implementations (`OfflineFirstArticleRepository`, `DataStoreUserPreferencesRepository`)
- `:app` wires everything: navigation host, Hilt entry point, theme

### State Management (UDF)

ViewModels expose `StateFlow<UiState>` to composables. UiState is a `sealed interface` with
`Loading`, `Success`, and `Error` variants. Composables are stateless — they receive state
and event callbacks as parameters. The screen composable handles ViewModel wiring; the content
composable is a separate private function that is fully stateless.

Pattern:
```
ViewModel.uiState: StateFlow<UiState>  ← stateIn(WhileSubscribed(5_000))
Screen composable  ← collectAsStateWithLifecycle()
Content composable ← receives state + lambdas, no ViewModel reference
```

### Offline-First Strategy

- `OfflineFirstArticleRepository` is the single implementation of `ArticleRepository`
- On initial load: emit cached data from Room immediately, then refresh from network in background
- `refreshArticles()` is private — called internally during `getArticles()` flow after emitting cached data
- Stale cache entries (non-bookmarked, older than 24 h) are purged after each refresh
- `ArticleDao.upsertPreservingBookmarks()` is a `@Transaction` that preserves bookmark state across upserts

### Room Database

- Schema exports live in `core/database/schemas/` — commit new JSON files when bumping DB version
- `fallbackToDestructiveMigration` is enabled (acceptable for cache-only data)

## Convention Plugins (build-logic/)

All module Gradle config is managed through convention plugins. Never duplicate SDK
versions or compiler options in individual `build.gradle.kts` files.

| Plugin ID | Class | Configures |
|---|---|---|
| `newsreader.android.application` | `AndroidApplicationConventionPlugin` | App module: AGP, Kotlin, Compose compiler, compileSdk/minSdk/targetSdk, JVM 17 |
| `newsreader.android.library` | `AndroidLibraryConventionPlugin` | Library modules: AGP, Kotlin, compileSdk/minSdk, JVM 17 |
| `newsreader.android.library.compose` | `AndroidComposeConventionPlugin` | Extends `android.library` + enables Compose build feature |
| `newsreader.android.hilt` | `AndroidHiltConventionPlugin` | Hilt + KSP wiring for any module |
| `newsreader.android.feature` | `AndroidFeatureConventionPlugin` | Feature modules: extends `library.compose` + `hilt`, adds `:core:ui/domain/model/common` + Navigation/Lifecycle deps |

Pure Kotlin modules (`:core:model`, `:core:domain`) use `alias(libs.plugins.kotlin.jvm)` directly
instead of any convention plugin.

## Code Conventions

### Kotlin

- `val` over `var` — immutable by default
- No `!!` operator — use safe calls (`?.`), `require()`, or `checkNotNull()`
- Named arguments for functions with 3+ parameters
- `when` over `if-else` chains for sealed types
- No `print()` or `println()` — use `timber.log.Timber`
- No comments unless explaining a non-obvious WHY

### Coroutines & Flow

- `StateFlow` only — never `LiveData`
- `collectAsStateWithLifecycle()` in composables — never `collectAsState()`
- `viewModelScope` in ViewModels
- `flowOn(Dispatchers.IO)` / `withContext(ioDispatcher)` for data layer work
- Never `GlobalScope`
- Use `emitAll()` to forward a DAO `Flow` inside a `flow {}` builder — never `collect {}` inside `flow {}`

### Jetpack Compose

- `modifier` is always the last parameter, named `modifier`, type `Modifier = Modifier`
- `MaterialTheme.colorScheme.*` only — no hardcoded colors
- `const` constructors wherever possible
- `LazyColumn`/`LazyRow` for lists — always provide a stable `key`
- Max ~40 lines per `build()` / composable body — extract subcomposables

### Navigation

- Type-safe Navigation Compose (Navigation 2.9.0+)
- Routes are `@Serializable data class` or `@Serializable data object`
- Non-optional route params become path parameters; optional ones become query params
- No string-based route definitions

### Serialization & Networking

- kotlinx.serialization everywhere — no Gson, no Moshi
- All API response models live in `:core:network`, annotated with `@Serializable`
- `@SerialName` for fields whose JSON key differs from the Kotlin property name

### Dependency Management

- All versions in `gradle/libs.versions.toml` — no hardcoded version strings in `build.gradle.kts`
- Use `alias(libs.plugins.*)` for plugins and `libs.*` for libraries

## Package Naming

```
com.hendramarihot.newsreader            ← :app
com.hendramarihot.newsreader.model      ← :core:model
com.hendramarihot.newsreader.network    ← :core:network
com.hendramarihot.newsreader.database   ← :core:database
com.hendramarihot.newsreader.data       ← :core:data
com.hendramarihot.newsreader.domain     ← :core:domain
com.hendramarihot.newsreader.ui         ← :core:ui
com.hendramarihot.newsreader.common     ← :core:common
com.hendramarihot.newsreader.feature.home       ← :feature:home
com.hendramarihot.newsreader.feature.detail     ← :feature:detail
com.hendramarihot.newsreader.feature.bookmarks  ← :feature:bookmarks
com.hendramarihot.newsreader.feature.settings   ← :feature:settings
```

## Adding a New Feature Module

1. Create directory: `feature/<name>/src/main/kotlin/com/hendramarihot/newsreader/feature/<name>/`
2. Create `feature/<name>/build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.newsreader.android.feature)
   }
   android {
       namespace = "com.hendramarihot.newsreader.feature.<name>"
   }
   ```
3. Register in `settings.gradle.kts`: `include(":feature:<name>")`
4. Add to `:app`'s `build.gradle.kts` dependencies
5. Create `<Name>Screen.kt`, `<Name>ViewModel.kt`, `<Name>UiState.kt`, `navigation/<Name>Navigation.kt`
6. Register the screen in `NewsReaderNavHost`
7. Add to `TopLevelDestination` enum if it's a bottom-nav destination

## Testing

- Unit tests: JUnit 5 (`@Test`, `assertThat`), MockK (`mockk`, `coEvery`, `coVerify`), Turbine (`test {}`) for Flow, `runTest` + `StandardTestDispatcher` for coroutines
- ViewModel tests: inject `UnconfinedTestDispatcher`, assert `StateFlow` values with Turbine
- Repository tests: mock DAO and API, verify cache-first behavior
- UI tests: `createComposeRule()` for composable interaction tests
- Test files mirror source: `src/test/kotlin/...` for unit, `src/androidTest/kotlin/...` for UI
- Run `./gradlew testDebugUnitTest` before every commit
