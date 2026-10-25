package com.hendramarihot.newsreader.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/** The `libs` version catalog, shared with the main build via build-logic/settings.gradle.kts. */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/** Reads an integer from `[versions]`, naming the alias when it is missing or malformed. */
internal fun VersionCatalog.versionInt(alias: String): Int {
    val raw = findVersion(alias).orElseThrow {
        GradleException("Version '$alias' is missing from gradle/libs.versions.toml")
    }.requiredVersion
    return raw.toIntOrNull()
        ?: throw GradleException("Version '$alias' in gradle/libs.versions.toml must be an integer, was '$raw'")
}

/** The Java release every module compiles for. Declared once as `java` in the catalog. */
internal val VersionCatalog.javaRelease: Int
    get() = versionInt("java")

internal val VersionCatalog.javaVersion: JavaVersion
    get() = JavaVersion.toVersion(javaRelease)

internal val VersionCatalog.jvmTarget: JvmTarget
    get() = JvmTarget.fromTarget(javaRelease.toString())
