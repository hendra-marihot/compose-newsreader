plugins {
    alias(libs.plugins.newsreader.android.feature)
}

android {
    namespace = "com.hendramarihot.newsreader.feature.settings"
}

dependencies {
    testImplementation(libs.bundles.testing)
}
