import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("newsreader.android.library.compose")
                apply("newsreader.android.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            val libs = libs()

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:common"))

                add("implementation", libs.findLibrary("hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("navigation-compose").get())
                add("implementation", libs.findLibrary("lifecycle-viewmodel-compose").get())
                add("implementation", libs.findLibrary("lifecycle-runtime-compose").get())
            }
        }
    }
}

internal fun Project.libs(): VersionCatalog =
    rootProject.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
