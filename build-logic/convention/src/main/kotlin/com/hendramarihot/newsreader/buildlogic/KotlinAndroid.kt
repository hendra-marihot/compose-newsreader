package com.hendramarihot.newsreader.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Android and Kotlin setup shared by the application and library plugins.
 *
 * Kotlin compilation comes from AGP's built-in Kotlin support (AGP 9+), so no
 * `org.jetbrains.kotlin.android` plugin is applied. SDK levels come from the
 * version catalog so they are declared once.
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = libs.versionInt("compileSdk")

        // CommonExtension exposes these as properties only. The `defaultConfig {}`
        // lambda form exists on the application and library subtypes.
        defaultConfig.minSdk = libs.versionInt("minSdk")

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    configureJUnitPlatform()
}

/**
 * Runs unit tests on the JUnit Platform (JUnit 5). Gradle 9 no longer puts the
 * platform launcher on the test runtime classpath, so it is declared here.
 */
internal fun Project.configureJUnitPlatform() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
    }
}
