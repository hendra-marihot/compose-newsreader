package com.hendramarihot.newsreader.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Android and Kotlin setup shared by the application and library plugins.
 *
 * Kotlin compilation comes from AGP's built-in Kotlin support (AGP 9+), so no
 * `org.jetbrains.kotlin.android` plugin is applied. SDK levels and the Java
 * release come from the version catalog so they are declared once.
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = libs.versionInt("compileSdk")

        // CommonExtension exposes these as properties only. The `defaultConfig {}`
        // lambda form exists on the application and library subtypes.
        defaultConfig.minSdk = libs.versionInt("minSdk")

        compileOptions.apply {
            sourceCompatibility = libs.javaVersion
            targetCompatibility = libs.javaVersion
        }
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        // Pin the JDK that runs the compilers so builds match across machines and
        // CI. Android modules compile against android.jar, so this is about
        // reproducibility, not about leaking newer JDK APIs.
        jvmToolchain(libs.javaRelease)
        compilerOptions {
            jvmTarget.set(libs.jvmTarget)
        }
    }

    configureJUnitPlatform()
}

/**
 * Runs unit tests on the JUnit Platform (JUnit 5). The engine and the launcher
 * are test infrastructure, so they are declared here once. Gradle 9 no longer
 * adds the launcher to the test runtime classpath on its own.
 */
internal fun Project.configureJUnitPlatform() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        add("testRuntimeOnly", libs.findLibrary("junit5-engine").get())
        add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
    }
}
