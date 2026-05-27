plugins {
    id("newsreader.android.feature")
}

android {
    namespace = "com.hendramarihot.newsreader.feature.settings"
}

dependencies {
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
}
