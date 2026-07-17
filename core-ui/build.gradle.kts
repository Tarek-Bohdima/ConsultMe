// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.compose")
    // No Hilt: :core-ui is deliberately DI-free (see README) so every feature
    // can consume its stateless composables without pulling in a Hilt graph.
}

android {
    namespace = "com.thecompany.consultme.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
