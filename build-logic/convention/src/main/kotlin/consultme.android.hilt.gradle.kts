// Copyright 2026 MyCompany
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val libs = the<LibrariesForLibs>()

dependencies {
    "implementation"(libs.hilt.android)
    "ksp"(libs.hilt.compiler)
    // KSP 2.3.10 defaults ksp.allow.all.target.configuration to false, so the
    // global ksp(...) no longer propagates to test variants. Declare the Hilt
    // processor explicitly for androidTest/test so their component codegen runs.
    "kspAndroidTest"(libs.hilt.compiler)
    "kspTest"(libs.hilt.compiler)
}
