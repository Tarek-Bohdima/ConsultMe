// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.compose")
    id("consultme.android.hilt")
}

android {
    namespace = "com.thecompany.consultme.feature.example"
}

dependencies {
    implementation(projects.coreData)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
