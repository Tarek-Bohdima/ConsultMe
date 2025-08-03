// Copyright 2025 MyCompany
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.gradle) // <-- ADDED
    alias(libs.plugins.ksp) // <-- ADDED
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xcontext-receivers"))
    }
}

android {
    namespace = "com.thecompany.consultme.core.database"
    compileSdk = 36

    defaultConfig {
        minSdk = 25
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner" // <-- CHANGED
        consumerProguardFiles("consumer-rules.pro")

        // KSP argument for Room schema location
        ksp {
            // <-- ADDED
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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
        // <-- ADDED
        compose = false // Database modules typically don't need Compose
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
    // Core & Hilt
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android) // <-- ADDED
    ksp(libs.hilt.compiler) // <-- ADDED

    // Room
    implementation(libs.androidx.room.runtime) // <-- ADDED
    implementation(libs.androidx.room.ktx) // <-- ADDED
    ksp(libs.androidx.room.compiler) // <-- ADDED (KSP for Room)

    // Testing - Now using :core-testing
    testImplementation(project(":core-testing")) // <-- ADDED
    androidTestImplementation(project(":core-testing")) // <-- ADDED

    // implementation(libs.androidx.appcompat) // <-- REMOVED
    // implementation(platform(libs.androidx.compose.bom)) // <-- REMOVED
    // implementation(libs.androidx.compose.material3) // <-- REMOVED
    // testImplementation(libs.junit) // <-- REMOVED
    // androidTestImplementation(libs.androidx.test.ext.junit) // <-- REMOVED
    // androidTestImplementation(libs.androidx.espresso.core) // <-- REMOVED
}
