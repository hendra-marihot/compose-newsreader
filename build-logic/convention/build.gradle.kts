plugins {
    `kotlin-dsl`
}

group = "com.hendramarihot.newsreader.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "newsreader.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "newsreader.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "newsreader.android.library.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "newsreader.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "newsreader.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
