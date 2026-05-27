plugins {
    alias(libs.plugins.newsreader.android.library)
    alias(libs.plugins.newsreader.android.hilt)
}

android {
    namespace = "com.hendramarihot.newsreader.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
