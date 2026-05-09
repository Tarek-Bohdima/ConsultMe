// Copyright 2026 MyCompany
import com.android.build.api.dsl.CommonExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

extensions.findByType<CommonExtension>()?.apply {
    buildFeatures.compose = true
}

dependencies {
    val bom = libs.androidx.compose.bom
    "implementation"(platform(bom))
    "androidTestImplementation"(platform(bom))
    "implementation"(libs.bundles.androidx.compose)
    "androidTestImplementation"(libs.androidx.compose.ui.test.junit4)
    "debugImplementation"(libs.bundles.androidx.compose.debug)
}
