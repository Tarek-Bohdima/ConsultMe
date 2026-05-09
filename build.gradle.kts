import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    // AGP 9+ bundles Kotlin support; `kotlin-android` plugin alias is removed
    // here and from `[plugins]` in `gradle/libs.versions.toml`.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.spotless) apply false
    // Adds the baselineprofile plugin to the root buildscript classpath so the
    // `consultme.android.application` and `consultme.android.baselineprofile`
    // convention plugins can apply it.
    alias(libs.plugins.androidx.baselineprofile) apply false
    // Kover applied at root aggregates coverage from every module that also
    // applies the consultme.kover convention. `./gradlew koverHtmlReport`
    // produces a project-wide report under `build/reports/kover/`.
    alias(libs.plugins.kover)
    // Registers `:moduleGraph` to (re)generate `docs/MODULE_GRAPH.md`.
    id("consultme.modulegraph")
}

// Adopters override these in gradle.properties (template.company / template.licenseYear).
// $YEAR is a Spotless token resolved at apply time; keep it as the default.
val templateCompany = (findProperty("template.company") as? String)?.takeIf { it.isNotBlank() } ?: "MyCompany"
val templateLicenseYear = (findProperty("template.licenseYear") as? String)?.takeIf { it.isNotBlank() } ?: "\$YEAR"
val licenseHeaderText = "// Copyright $templateLicenseYear $templateCompany"

subprojects {
    plugins.apply(rootProject.libs.plugins.spotless.get().pluginId)

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
}

// Auto-aggregate Kover reports: every subproject that applies the
// `consultme.kover` convention gets pulled into the root report.
// New modules participate without editing this block.
subprojects {
    plugins.withId("org.jetbrains.kotlinx.kover") {
        rootProject.dependencies.add("kover", this@subprojects)
    }
}
