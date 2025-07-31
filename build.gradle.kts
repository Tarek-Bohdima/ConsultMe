import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension // <-- Add this import at the top

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false

    // Both should have apply false here.
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
}

// This block configures settings for all modules that have the plugins applied.
subprojects {
    // Apply both plugins to each sub-project
    plugins.apply(rootProject.libs.plugins.spotless.get().pluginId)
    plugins.apply(rootProject.libs.plugins.detekt.get().pluginId)

    // Configure Spotless
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/camera/viewfinder/**")
            ktlint(libs.ktlint.get().version)
        }
        kotlinGradle {
            ktlint(libs.ktlint.get().version)
        }
    }

    // Configure Detekt using the same explicit pattern as Spotless
    configure<DetektExtension> {
        config.setFrom(files("$rootDir/config/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
    }
}
