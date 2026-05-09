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
    implementation(projects.featureExample)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
