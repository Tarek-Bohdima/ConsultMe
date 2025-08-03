// Copyright 2025 MyCompany
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.gradle) // <-- ADDED
    alias(libs.plugins.ksp) // <-- ADDED
    alias(libs.plugins.compose.compiler) // <-- ADDED/ENSURED
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xcontext-receivers"))
    }
}

android {
    namespace = "com.thecompany.consultme.core.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 25
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner" // <-- CHANGED
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        // <-- ADDED/ENSURED
        compose = true
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    lint {
        // Assuming you want to keep the lint configuration as is
        baseline = file("lint-baseline.xml")
        quiet = true
        checkAllWarnings = true
        warningsAsErrors = false // As per your other files
        textReport = true
        htmlReport = true
        xmlReport = false
        checkReleaseBuilds = true
        abortOnError = true
        checkDependencies = true
    }
}

dependencies {
    // Core & Hilt
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android) // <-- ADDED
    ksp(libs.hilt.compiler) // <-- ADDED

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui) // <-- ADDED
    implementation(libs.androidx.ui.graphics) // <-- Assuming needed if UI module provides graphics elements
    implementation(libs.androidx.compose.ui.tooling.preview) // <-- ADDED
    implementation(libs.androidx.compose.material3)

    // Testing - Now using :core-testing
    testImplementation(project(":core-testing")) // <-- ADDED
    androidTestImplementation(project(":core-testing")) // <-- ADDED

    // androidTestImplementation(libs.androidx.test.ext.junit) // <-- REMOVED
    // androidTestImplementation(libs.androidx.espresso.core) // <-- REMOVED
    // testImplementation(libs.junit) // <-- REMOVED

    // implementation(libs.androidx.appcompat) // <-- REMOVED (likely not needed for a pure Compose module)
}
