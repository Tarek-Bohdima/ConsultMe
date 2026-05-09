// Copyright 2026 MyCompany
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("consultme.android.library")
    id("consultme.android.compose")
    id("consultme.android.hilt")
}

val libs = the<LibrariesForLibs>()

dependencies {
    "implementation"(project(":core-designsystem"))
    "implementation"(project(":core-ui"))

    "implementation"(libs.androidx.lifecycle.runtime.compose)
    "implementation"(libs.androidx.lifecycle.viewmodel.compose)
    "implementation"(libs.androidx.hilt.navigation.compose)

    "testImplementation"(project(":core-testing"))
    "androidTestImplementation"(project(":core-testing"))
}
