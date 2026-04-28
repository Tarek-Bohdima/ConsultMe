// Copyright 2025 MyCompany
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xcontext-receivers"))
    }
}

hilt {
    enableAggregatingTask = true
}

android {
    namespace = "com.thecompany.consultme.core.database"
    compileSdk = 36

    defaultConfig {
        minSdk = 25
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner"
        consumerProguardFiles("consumer-rules.pro")

        ksp {
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
        compose = false
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))
}
