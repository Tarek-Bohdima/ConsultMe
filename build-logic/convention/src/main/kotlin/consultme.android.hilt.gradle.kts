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
}
