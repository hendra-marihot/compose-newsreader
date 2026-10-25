plugins {
    alias(libs.plugins.newsreader.jvm.library)
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
}
