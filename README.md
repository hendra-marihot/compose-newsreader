# Compose NewsReader

A multi-module Android news reader built with Jetpack Compose, structured around offline-first data flow and strict layer boundaries. This is an architecture showcase, not a production app — the goal is to demonstrate the kind of structural thinking that matters on a team, using a domain simple enough to stay out of the way.

I'm primarily a Flutter engineer. This project is part of a broader portfolio showing I can operate at a senior level across mobile platforms, not just in my primary stack. Other repositories cover different aspects of mobile engineering; this one focuses on modular architecture, cache-first data flow, and build system design in native Android.

<!-- TODO: Add a GIF showing the main flow — browse categories, open article, toggle bookmark, check bookmarks tab. A single ~10s screen recording converted to GIF covers everything. -->

## Architecture

The app is split into 12 Gradle modules across four layers. The dependency graph is strictly top-down — feature modules never depend on each other, and the domain layer has zero Android framework dependencies.

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

`:core:model` and `:core:domain` are pure Kotlin/JVM modules — no Android dependencies at all. Domain owns the repository interfaces; data provides the implementations. This means domain can be tested without Robolectric or an Android runtime, and the boundary is enforced by the build system, not just convention.

The module structure is managed by five convention plugins in `build-logic/`. A single `AndroidFeatureConventionPlugin` applies Compose, Hilt, and all `:core:*` dependencies for any feature module — the feature's `build.gradle.kts` is four lines. This eliminates the copy-paste Gradle that tends to drift across modules over time.

## Key decisions

**Offline-first with `channelFlow`.**
The repository emits cached data from Room immediately, then launches a concurrent network refresh. When fresh data arrives, Room's reactive `Flow` emits again automatically. I used `channelFlow` because a regular `flow {}` can't launch concurrent coroutines — `channelFlow` provides a `SendChannel` that both the DAO's reactive stream and the refresh coroutine can write to. The tradeoff is slightly more complexity than a sequential emit-cached-then-emit-refreshed approach, but the UI never blocks on the network.

**SHA-256 article IDs.**
NewsAPI returns article URLs as the only stable identifier, but URLs contain query parameters, slashes, and fragments that break Navigation Compose route parsing. The mapper hashes each URL with SHA-256, truncated to 12 hex chars, producing a deterministic, navigation-safe ID. The original URL is preserved in a separate field for browser/share features. This is tested explicitly — five tests in `MappersTest` verify stability, uniqueness, and the absence of special characters.

**Bookmark-preserving upserts.**
When the app refreshes articles from the network, Room's `@Upsert` would overwrite the `isBookmarked` field (which only exists locally). The DAO handles this with a `@Transaction` method: read which IDs are currently bookmarked, upsert the batch, then restore the bookmark flags. This keeps the three-step operation atomic. Stale non-bookmarked articles are purged after 24 hours; bookmarked articles are kept indefinitely.

**Convention plugins over copy-paste Gradle.**
With 12 modules, duplicating `compileSdk`, `minSdk`, Compose compiler config, and Hilt wiring in every `build.gradle.kts` would be a maintenance problem. The `build-logic/` included build defines five plugins that compose together — `AndroidFeatureConventionPlugin` applies `library.compose` + `hilt` + all core dependencies in one line. Version management is fully centralized in `libs.versions.toml`.

**kotlinx.serialization over Moshi.**
The project uses KSP everywhere — Hilt, Room, and serialization all run through the same annotation processing pipeline. kotlinx.serialization is Kotlin-native, avoids adding Moshi's reflection or codegen as a separate dependency, and is the natural fit for a KSP-only build. The tradeoff: Moshi has better error messages for malformed JSON, which matters more in production than in a portfolio project.

**Unidirectional data flow.**
Every ViewModel exposes a single `StateFlow<UiState>` with a sealed interface (`Loading`, `Success`, `Error`). Screen composables only wire the ViewModel; a separate stateless content composable receives state and event lambdas. This separation makes the UI testable without a ViewModel and keeps composable previews straightforward.

## Testing strategy

Tests focus on the layers where logic lives and bugs hide: the repository, the mappers, and the ViewModels.

| Layer | Files | What's tested |
|---|---|---|
| Repository | `OfflineFirstArticleRepositoryTest` (8 tests) | Cache-first emission order, graceful network failure, bookmark toggle, search with URL filtering |
| Mappers | `MappersTest` (5 tests) | ID stability, determinism, uniqueness, field mapping, navigation safety |
| ViewModels | 4 test files (14 tests total) | State transitions, category switching, bookmark toggling, preference propagation |
| Use cases | `UseCaseTests` (4 tests) | Delegation correctness |

The use case tests are intentionally thin — each use case is a single-method delegator to a repository, so the tests verify wiring, not logic. The real coverage is at the repository level, where the offline-first behavior, concurrent refresh, and error handling actually happen.

**What's not tested:** There are no Compose UI tests or instrumented tests. The current test suite runs entirely on the JVM via `./gradlew testDebugUnitTest`, which keeps CI fast. UI tests would add value for interaction regressions but aren't the focus of this project.

## Non-goals

These are things I deliberately left out, not things I forgot:

- **Pagination.** NewsAPI's free tier returns a small result set. Adding paging would complicate the repository and DAO for minimal architectural insight beyond what the offline-first pattern already demonstrates.
- **Pull-to-refresh / swipe gestures.** The refresh happens automatically on each subscription. Adding a manual trigger is a UI concern, not an architecture one.
- **WorkManager background sync.** Would be the right choice for a production app, but doesn't demonstrate anything the current `channelFlow`-based refresh doesn't already show about concurrent data flow.
- **Error retry with exponential backoff.** The repository silently falls back to cached data on network failure. A retry strategy would be a good addition but is orthogonal to the architecture patterns this project showcases.
- **Baseline profiles and R8 rules.** Performance optimization is valuable but out of scope for an architecture-focused project.

## What I'd change

If I were rebuilding this, I'd reconsider a few things:

- **The `channelFlow` pattern is subtle.** It works correctly, but a new team member reading the repository for the first time would need to trace through `channelFlow` + `launch` + `collect` + `send` to understand the data flow. A `combine`-based approach with a separate refresh trigger `Flow` might be more readable, even if slightly less elegant.
- **No pagination makes the feed feel static.** Even without a business need, paging is such a common Android topic that having a `RemoteMediator` implementation would strengthen the portfolio.
- **The search flow is network-only.** `getArticles` uses the offline-first pattern, but `searchArticles` goes straight to the network with no cache fallback. Consistency would be better — either cache search results too, or make the asymmetry more intentional.

## Setup

This project uses [NewsAPI](https://newsapi.org/) for article data. Add your API key to `local.properties`:

```properties
NEWS_API_KEY=your_api_key_here
```

Build and run tests:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Requires JDK 17. Targets Android API 26+ (compileSdk 35).

## Related repositories

This is one piece of a portfolio covering different aspects of senior mobile engineering:

| Repository | Focus area |
|---|---|
| **compose-newsreader** (this repo) | Multi-module Android, Hilt, Room, offline-first |
| [android-design-system](https://github.com/hendra-marihot/android-design-system) | Compose component library, design tokens |
| [compose-performance-lab](https://github.com/hendra-marihot/compose-performance-lab) | Compose performance anti-patterns and fixes |
| [kmp-currency-converter](https://github.com/hendra-marihot/kmp-currency-converter) | Kotlin Multiplatform, shared business logic |
| [flutter-expense-tracker](https://github.com/hendra-marihot/flutter-expense-tracker) | Clean Architecture, Riverpod, Drift, Material 3 |
| [flutter-ui-toolkit](https://github.com/hendra-marihot/flutter-ui-toolkit) | Reusable widgets, shimmer, adaptive layouts |
| [flutter-platform-bridge](https://github.com/hendra-marihot/flutter-platform-bridge) | Platform channels, native interop |
| [mobile-ci-cd-templates](https://github.com/hendra-marihot/mobile-ci-cd-templates) | CI/CD, release automation, infrastructure |

## License

```
Copyright 2026 Hendra Marihot

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
