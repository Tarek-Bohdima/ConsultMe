/*
 * Copyright 2025 MyCompany
 */
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xcontext-receivers"))
    }
}

android {
    namespace = "com.thecompany.consultme.core.testing"
    compileSdk = 36

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    lint {
        baseline = file("lint-baseline.xml")
        // --- Add these lines to be more explicit ---
        quiet = true
        checkAllWarnings = true

        // This is a strict setting that will elevate all warnings to errors.
        // This will force them to appear in the report.
        // You may want to set this to 'false' again later.
        warningsAsErrors = false

        // --- Keep the previous settings ---
        textReport = true
        htmlReport = true
        xmlReport = false
        checkReleaseBuilds = true
        abortOnError = true
        checkDependencies = true
    }
}

dependencies {
    // Dependencies required by this module's own code (e.g., HiltTestRunner)
    implementation(libs.androidx.test.runner) // For AndroidJUnitRunner
    implementation(libs.hilt.android.testing) // For HiltTestApplication

    // Common test dependencies exposed to other modules
    api(libs.junit)
    api(libs.androidx.test.ext.junit) // Alias for androidx.test.ext:junit
    api(libs.androidx.espresso.core)
}
