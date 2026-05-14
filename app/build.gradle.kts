// Copyright 2025 MyCompany
plugins {
    id("consultme.android.application")
    id("consultme.android.compose")
    id("consultme.android.hilt")
}

android {
    namespace = "com.thecompany.consultme"

    defaultConfig {

        applicationId = "com.thecompany.consultme"
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.coreDesignsystem)
    implementation(projects.coreUi)
    // TODO(template): replace projects.featureExample with your real feature
    // module(s) before shipping. The launcher activity in :app currently
    // renders `ExampleScreen` from this placeholder — see feature-example/README.md.
    implementation(projects.featureExample)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Runtime side of the baseline-profile pipeline. Without this dep, the
    // committed `baseline-prof.txt` ships in the APK but is never installed
    // on user devices.
    implementation(libs.androidx.profileinstaller)

    // Producer module that generates `baseline-prof.txt`. Regenerate via
    // `./gradlew :app:generateReleaseBaselineProfile`.
    "baselineProfile"(projects.baselineprofile)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
