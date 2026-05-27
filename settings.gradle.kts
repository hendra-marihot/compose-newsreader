pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NewsReader"

include(":app")

include(":core:model")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:common")

include(":feature:home")
include(":feature:detail")
include(":feature:bookmarks")
include(":feature:settings")
