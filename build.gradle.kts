import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension // <-- Add this import at the top

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.gradle) apply false
//    alias(libs.plugins.kotlin.kapt) apply false

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

            // Define and apply the license header for Kotlin files
            // This is the robust, correct way to apply the license header.
            // It places the header AFTER the package declaration.
            licenseHeader(
                """
                // Copyright ${'$'}YEAR MyCompany
                """.trimIndent(),
                "package " // The delimiter tells Spotless to look for the line starting with "package "
            )

            ktlint(libs.ktlint.get().version)
        }
        kotlinGradle {
            target("*.gradle.kts")

            // Define and apply the license header for Gradle files
            // For Gradle files, there's no package declaration, so we don't need a delimiter.
            // It will be placed at the top of the file.
            licenseHeader(
                """
                // Copyright ${'$'}YEAR MyCompany
                """.trimIndent(),
                  "/*"
            )

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
