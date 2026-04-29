import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
}

// Adopters override these in gradle.properties (template.company / template.licenseYear).
// $YEAR is a Spotless token resolved at apply time; keep it as the default.
val templateCompany = (findProperty("template.company") as? String)?.takeIf { it.isNotBlank() } ?: "MyCompany"
val templateLicenseYear = (findProperty("template.licenseYear") as? String)?.takeIf { it.isNotBlank() } ?: "\$YEAR"
val licenseHeaderText = "// Copyright $templateLicenseYear $templateCompany"

subprojects {
    plugins.apply(rootProject.libs.plugins.spotless.get().pluginId)
    plugins.apply(rootProject.libs.plugins.detekt.get().pluginId)

    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            // Header is placed AFTER the `package` declaration via the delimiter.
            licenseHeader(licenseHeaderText, "package ")
            ktlint(libs.ktlint.get().version)
        }
        kotlinGradle {
            target("*.gradle.kts")
            // No `package` line in Gradle scripts; place header above the first `/*`.
            licenseHeader(licenseHeaderText, "/*")
            ktlint(libs.ktlint.get().version)
        }
    }

    configure<DetektExtension> {
        config.setFrom(files("$rootDir/config/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
    }
}
