plugins {
    alias(libs.plugins.newsreader.android.library.compose)
}

android {
    namespace = "com.hendramarihot.newsreader.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    api(libs.compose.material.icons.extended)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    debugImplementation(libs.compose.ui.tooling)
}
