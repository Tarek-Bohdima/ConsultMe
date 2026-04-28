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

subprojects {
    plugins.apply(rootProject.libs.plugins.spotless.get().pluginId)
    plugins.apply(rootProject.libs.plugins.detekt.get().pluginId)

    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            // Header is placed AFTER the `package` declaration via the delimiter.
            licenseHeader(
                """
                // Copyright ${'$'}YEAR MyCompany
                """.trimIndent(),
                "package ",
            )
            ktlint(libs.ktlint.get().version)
        }
        kotlinGradle {
            target("*.gradle.kts")
            // No `package` line in Gradle scripts; place header above the first `/*`.
            licenseHeader(
                """
                // Copyright ${'$'}YEAR MyCompany
                """.trimIndent(),
                "/*",
            )
            ktlint(libs.ktlint.get().version)
        }
    }

    configure<DetektExtension> {
        config.setFrom(files("$rootDir/config/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
    }
}
