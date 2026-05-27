plugins {
    alias(libs.plugins.newsreader.android.library)
    alias(libs.plugins.newsreader.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.hendramarihot.newsreader.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.kotlinx.coroutines.android)
}
