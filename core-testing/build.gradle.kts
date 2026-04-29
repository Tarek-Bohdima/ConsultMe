// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
}

android {
    namespace = "com.thecompany.consultme.core.testing"

    // Override the convention default — :core-testing provides HiltTestRunner,
    // so it can't depend on its own runner.
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
