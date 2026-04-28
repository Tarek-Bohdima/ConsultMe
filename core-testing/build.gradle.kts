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
    // Required for HiltTestRunner; not exposed because consumers don't extend AndroidJUnitRunner.
    implementation(libs.androidx.test.runner)

    // Re-exported via api() so consumers don't redeclare these in every module.
    api(libs.junit)
    api(libs.androidx.test.core)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.test.runner)
    api(libs.androidx.espresso.core)

    api(libs.hilt.android.testing)

    api(libs.kotlinx.coroutines.test)

    api(libs.mockk.core)
    api(libs.mockk.android)

    api(libs.turbine)
    api(libs.truth)
}
