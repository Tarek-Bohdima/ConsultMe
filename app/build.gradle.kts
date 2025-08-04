// Copyright 2025 MyCompany
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
//    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.compose.compiler)
}
// ADDED Hilt configuration block
hilt {
    enableAggregatingTask = true
}
// Enable room auto-migrations
// Configure KSP arguments, including androidx . room . Room schema location
// ksp {
//     arg("room.schemaLocation", "$projectDir/schemas")
// }

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xcontext-receivers"))
    }
}

android {
    namespace = "com.thecompany.consultme"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thecompany.consultme"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner" // <-- CHANGED

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
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
        compose = true
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(project(":core-ui"))
    implementation(project(":feature-chat"))

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // Correct: Hilt KSP
//    kapt(libs.hilt.compiler)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Testing - Now using :core-testing
    testImplementation(project(":core-testing")) // <-- CORRECTED
    androidTestImplementation(project(":core-testing")) // <-- CORRECTED

    // androidTestImplementation(libs.androidx.test.ext.junit) // <-- REMOVED (provided by :core-testing)
    // androidTestImplementation(libs.androidx.espresso.core) // <-- REMOVED (provided by :core-testing)
    // testImplementation(libs.junit) // <-- REMOVED (provided by :core-testing)

    // Compose specific testing - Stays here
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
