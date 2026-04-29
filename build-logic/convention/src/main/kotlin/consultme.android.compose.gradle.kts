// Copyright 2026 MyCompany
import com.android.build.api.dsl.CommonExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

extensions.findByType<CommonExtension<*, *, *, *, *, *>>()?.apply {
    buildFeatures.compose = true
}

dependencies {
    val bom = libs.androidx.compose.bom
    "implementation"(platform(bom))
    "androidTestImplementation"(platform(bom))
    "implementation"(libs.androidx.compose.ui)
    "implementation"(libs.androidx.ui.graphics)
    "implementation"(libs.androidx.compose.ui.tooling.preview)
    "implementation"(libs.androidx.compose.material3)
    "androidTestImplementation"(libs.androidx.compose.ui.test.junit4)
    "debugImplementation"(libs.androidx.compose.ui.tooling)
    "debugImplementation"(libs.androidx.compose.ui.test.manifest)
}
