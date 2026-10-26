# Compose NewsReader

[![CI](https://github.com/hendra-marihot/compose-newsreader/actions/workflows/ci.yml/badge.svg)](https://github.com/hendra-marihot/compose-newsreader/actions/workflows/ci.yml)
[![License: Apache-2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

A multi-module Android news reader built with Jetpack Compose. It shows cached articles first and refreshes from the network in the background. The code shows how I structure a native Android app for a team: strict module boundaries, one-way data flow, and build logic that does not drift.

This repo is one of eight on senior mobile engineering. See [Related repositories](#related-repositories).

## Architecture

12 Gradle modules in four layers. Dependencies point down only. Feature modules never depend on each other. The domain layer has no Android dependencies.

```
:app
 ├── :feature:home
 ├── :feature:detail
 ├── :feature:bookmarks
 └── :feature:settings
      └── :core:ui  :core:domain  :core:common
           └── :core:data
                ├── :core:network
                └── :core:database
                     └── :core:model
```

`:core:model` and `:core:domain` are plain Kotlin/JVM modules. Domain owns the repository interfaces. Data provides the implementations. Domain code runs in tests without an Android runtime, and the build enforces the boundary.

Six convention plugins in `build-logic/` manage module setup. `AndroidFeatureConventionPlugin` applies Compose, Hilt and every `:core:*` dependency, so a feature's `build.gradle.kts` is four lines long.

## Key decisions

**Offline-first with `channelFlow`.** The repository emits cached rows from Room right away, then starts a network refresh in a child coroutine. When new rows land in Room, the reactive query emits again. A plain `flow {}` cannot launch concurrent work, so `channelFlow` provides the channel that both the DAO stream and the refresh write to. The UI never waits on the network. The cost is a pattern that takes a minute to read.

**SHA-256 article IDs.** NewsAPI has no stable ID other than the article URL, and URLs break Navigation Compose routes. The mapper hashes each URL with SHA-256 and keeps 12 hex characters. The ID is deterministic and safe in a route. The original URL stays in its own field. Five tests in `MappersTest` cover stability, uniqueness and character safety.

**Bookmark-preserving upserts.** A plain `@Upsert` would wipe the local `isBookmarked` flag on every refresh. The DAO runs a `@Transaction`: read the bookmarked IDs, upsert the batch, restore the flags. Non-bookmarked articles older than 24 hours are purged. Bookmarked ones stay.

**Convention plugins instead of copied Gradle.** With 12 modules, repeating `compileSdk`, `minSdk`, Compose and Hilt setup in every build file is a maintenance problem. The `build-logic/` included build defines six plugins that compose. SDK levels and library versions live in `libs.versions.toml`. Kotlin compilation comes from AGP 9's built-in Kotlin support, so no module applies the Kotlin Android plugin.

**kotlinx.serialization instead of Moshi.** Hilt, Room and serialization all run through KSP. kotlinx.serialization is Kotlin-native and needs no extra processor. Moshi gives better error messages for bad JSON, which matters more in production than here.

**One-way data flow.** Each ViewModel exposes one `StateFlow<UiState>` with `Loading`, `Success` and `Error` variants. The screen composable wires the ViewModel. A separate stateless composable takes state and callbacks. Previews stay simple and the UI is testable without a ViewModel.

## Tests

33 JVM tests. They sit where the logic is: the repository, the use cases, the mappers and the ViewModels.

| Layer | Files | Covers |
|---|---|---|
| Repository | `OfflineFirstArticleRepositoryTest` (9) | Cache-first emission order, network failure fallback, bookmark toggle, search |
| Mappers | `MappersTest` (5) | ID stability, uniqueness, field mapping, route safety |
| Use cases | `UseCaseTests` (4) | Each use case delegates to the repository |
| ViewModels | 4 files (14) | State transitions, category switching, bookmarks, preferences |
| Navigation | `HomeRouteSerializationTest` (1) | Route serialization |

There are no Compose UI tests or instrumented tests. Everything runs with `./gradlew testDebugUnitTest`, which keeps CI fast.

## Out of scope

- **Pagination.** NewsAPI's free tier returns a small set. Paging adds DAO and repository complexity without a new lesson.
- **Pull-to-refresh.** The refresh runs on every subscription.
- **WorkManager sync.** Right for production, but it shows nothing the current refresh does not.
- **Retry with backoff.** The repository falls back to cache on failure. Retry would be a good addition.
- **Baseline profiles and R8 rules.** Out of scope for an architecture repo.

## What I would change

- **`channelFlow` is subtle.** A `combine` of the DAO stream and a refresh trigger would be easier to read.
- **The feed feels static without paging.** A `RemoteMediator` would be a useful addition.
- **Search is network-only.** `getArticles` is cache-first, `searchArticles` is not. The two should match.

## Setup

Add a [NewsAPI](https://newsapi.org/) key to `local.properties`:

```properties
NEWS_API_KEY=your_api_key_here
```

Build and test:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Needs a JDK 17 or newer to run Gradle and the API 37 platform (`platforms;android-37.0`). If the local JDK is newer than 17, the toolchain resolver downloads JDK 17 for compilation. AGP installs the platform when the SDK licenses are accepted. Builds with Gradle 9.7, AGP 9.3 and Kotlin 2.4. Targets Android API 26 to 36 and compiles against API 37.

## Related repositories

Each repo in this set covers one area of mobile engineering.

| Repository | Focus |
|---|---|
| **compose-newsreader** (this repo) | Multi-module Android, Hilt, Room, offline-first |
| [android-design-system](https://github.com/hendra-marihot/android-design-system) | Compose component library, design tokens |
| [compose-performance-lab](https://github.com/hendra-marihot/compose-performance-lab) | Compose performance anti-patterns and fixes |
| [kmp-currency-converter](https://github.com/hendra-marihot/kmp-currency-converter) | Kotlin Multiplatform, shared business logic |
| [flutter-expense-tracker](https://github.com/hendra-marihot/flutter-expense-tracker) | Clean Architecture, Riverpod, Drift, Material 3 |
| [flutter-ui-toolkit](https://github.com/hendra-marihot/flutter-ui-toolkit) | Reusable widgets, shimmer, adaptive layouts |
| [flutter-platform-bridge](https://github.com/hendra-marihot/flutter-platform-bridge) | Platform channels, native interop |
| [mobile-ci-cd-templates](https://github.com/hendra-marihot/mobile-ci-cd-templates) | CI/CD, release automation |

## License

Copyright 2026 Hendra Marihot. Licensed under the [Apache License 2.0](LICENSE).
