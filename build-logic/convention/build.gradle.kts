// Copyright 2026 MyCompany
plugins {
    `kotlin-dsl`
}

group = "com.thecompany.consultme.buildlogic"

kotlin {
    jvmToolchain(17)
}

// Every `libs.*` accessor below pulls from `gradle/libs.versions.toml` — the
// same catalog every module uses. Keeping `build-logic`'s plugin classpath
// in lockstep with the catalog (rather than declaring versions here directly)
// is what prevents version drift between the conventions and the modules
// they configure, and is what makes Dependabot's grouped updates affect both
// layers atomically.
dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.kover.gradle.plugin)
    compileOnly(libs.androidx.baselineprofile.gradle.plugin)
    compileOnly(libs.roborazzi.gradle.plugin)

    // Expose the `libs` version-catalog accessor inside precompiled script plugins.
    // See https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
