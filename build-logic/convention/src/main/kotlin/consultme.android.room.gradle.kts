// Copyright 2026 MyCompany
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.google.devtools.ksp")
}

val libs = the<LibrariesForLibs>()

extensions.configure<KspExtension> {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    "implementation"(libs.androidx.room.runtime)
    "implementation"(libs.androidx.room.ktx)
    "ksp"(libs.androidx.room.compiler)
}
