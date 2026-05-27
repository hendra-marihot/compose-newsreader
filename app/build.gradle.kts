plugins {
    id("newsreader.android.application")
    id("newsreader.android.hilt")
}

android {
    namespace = "com.hendramarihot.newsreader"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.hendramarihot.newsreader"
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":feature:home"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:bookmarks"))
    implementation(project(":feature:settings"))
    implementation(libs.navigation.compose)
    implementation(libs.compose.material3)
    implementation(libs.timber)
}
