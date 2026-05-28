plugins {
    id("newsreader.android.feature")
}

android {
    namespace = "com.hendramarihot.newsreader.feature.bookmarks"
}

dependencies {
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
}
