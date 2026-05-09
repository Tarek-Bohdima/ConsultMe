// Copyright 2025 MyCompany
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("consultme.android.test")
    id("androidx.baselineprofile")
}

val libs = the<LibrariesForLibs>()

dependencies {
    "implementation"(libs.androidx.test.ext.junit)
    "implementation"(libs.androidx.test.uiautomator)
    "implementation"(libs.androidx.benchmark.macro.junit4)
}
