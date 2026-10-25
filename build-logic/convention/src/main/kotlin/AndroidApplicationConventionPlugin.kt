import com.android.build.api.dsl.ApplicationExtension
import com.hendramarihot.newsreader.buildlogic.configureKotlinAndroid
import com.hendramarihot.newsreader.buildlogic.libs
import com.hendramarihot.newsreader.buildlogic.versionInt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk = libs.versionInt("targetSdk")
                }

                buildFeatures {
                    compose = true
                }
            }
        }
    }
}
