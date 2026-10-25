plugins {
    alias(libs.plugins.newsreader.android.feature)
}

android {
    namespace = "com.hendramarihot.newsreader.feature.detail"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.coil.compose)

    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
}
