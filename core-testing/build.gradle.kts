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
    // Bundle definition lives in gradle/libs.versions.toml under [bundles].test-shared.
    api(libs.bundles.test.shared)
}
