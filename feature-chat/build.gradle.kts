// Copyright 2025 MyCompany
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.gradle) // <-- ENSURED/ADDED
    alias(libs.plugins.ksp) // <-- ENSURED/ADDED
    alias(libs.plugins.compose.compiler) // <-- ENSURED/ADDED
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xcontext-receivers"))
    }
}

android {
    namespace = "com.thecompany.consultme.feature.chat" // <-- VERIFIED
    compileSdk = 36

    defaultConfig {
        minSdk = 25
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner" // <-- CHANGED
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false // As per other modules
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        // <-- ENSURED/ADDED
        compose = true // Feature module provides UI
        aidl = false
        buildConfig = false // Usually false for feature modules
        renderScript = false
        shaders = false
    }

    lint {
        // Kept your existing lint configuration
        baseline = file("lint-baseline.xml")
        quiet = true
        checkAllWarnings = true
        warningsAsErrors = false
        textReport = true
        htmlReport = true
        xmlReport = false
        checkReleaseBuilds = true
        abortOnError = true
        checkDependencies = true
    }
}

dependencies {
    // Module dependencies
    implementation(project(":core-data")) // <-- ADDED (Features usually need data)
    // implementation(project(":core-ui")) // Usually not a direct dependency for features; app module composes

    // Core & Hilt
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Arch Components (ViewModels, Lifecycle for the feature)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Testing - Now using :core-testing
    testImplementation(project(":core-testing")) // <-- ADDED/ENSURED
    androidTestImplementation(project(":core-testing")) // <-- ADDED/ENSURED

    // Compose specific testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM for test artifacts
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // implementation(libs.androidx.appcompat) // <-- REMOVED
    // testImplementation(libs.junit) // <-- REMOVED
    // androidTestImplementation(libs.androidx.test.ext.junit) // <-- REMOVED
    // androidTestImplementation(libs.androidx.espresso.core) // <-- REMOVED
}
