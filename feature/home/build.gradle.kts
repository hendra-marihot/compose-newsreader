plugins {
    id("newsreader.android.feature")
}

android {
    namespace = "com.hendramarihot.newsreader.feature.home"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:common"))

    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
}
