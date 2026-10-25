import com.hendramarihot.newsreader.buildlogic.configureJUnitPlatform
import com.hendramarihot.newsreader.buildlogic.javaRelease
import com.hendramarihot.newsreader.buildlogic.jvmTarget
import com.hendramarihot.newsreader.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/** Pure Kotlin/JVM modules with no Android dependency (`:core:model`, `:core:domain`). */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            extensions.configure<KotlinJvmProjectExtension> {
                // Sets the Java toolchain too, so javac and kotlinc use the same JDK.
                jvmToolchain(libs.javaRelease)
                compilerOptions {
                    jvmTarget.set(libs.jvmTarget)
                }
            }

            configureJUnitPlatform()

            // Let `./gradlew testDebugUnitTest` cover JVM modules too, so CI and the
            // README need one command for the whole build.
            tasks.register("testDebugUnitTest") {
                group = "verification"
                description = "Alias for `test` so JVM modules run alongside Android unit tests."
                dependsOn("test")
            }
        }
    }
}
