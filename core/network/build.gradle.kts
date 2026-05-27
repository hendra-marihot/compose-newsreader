import java.util.Properties

plugins {
    alias(libs.plugins.newsreader.android.library)
    alias(libs.plugins.newsreader.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hendramarihot.newsreader.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) load(file.inputStream())
        }
        val apiKey = localProperties.getProperty("NEWS_API_KEY", "")
            .ifEmpty { System.getenv("NEWS_API_KEY") ?: "" }
        buildConfigField("String", "NEWS_API_KEY", "\"$apiKey\"")
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
}
