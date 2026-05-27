plugins {
    alias(libs.plugins.newsreader.android.library)
    alias(libs.plugins.newsreader.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hendramarihot.newsreader.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))

    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)

    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
}
