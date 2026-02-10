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
    api(libs.bundles.test.unit)

    api(libs.androidx.test.core)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.test.runner)
    api(libs.androidx.espresso.core)
    api(libs.hilt.android.testing)

    api(libs.mockk.android) {
        exclude(group = "io.mockk", module = "mockk-agent-jvm")
        exclude(group = "io.mockk", module = "mockk-agent")
    }
}
