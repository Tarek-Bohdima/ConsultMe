// Copyright 2026 MyCompany
import dagger.hilt.android.plugin.HiltExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val libs = the<LibrariesForLibs>()

extensions.configure<HiltExtension> {
    enableAggregatingTask = true
}

dependencies {
    "implementation"(libs.hilt.android)
    "ksp"(libs.hilt.compiler)
}
