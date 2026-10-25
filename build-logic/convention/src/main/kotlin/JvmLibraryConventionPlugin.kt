import com.hendramarihot.newsreader.buildlogic.configureJUnitPlatform
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/** Pure Kotlin/JVM modules with no Android dependency (`:core:model`, `:core:domain`). */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
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
