// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.hilt")
}

android {
    namespace = "com.thecompany.consultme.core.data"
}

dependencies {
    // Owns the ExampleRepository port; core-model comes transitively (api).
    implementation(projects.coreDomain)
    implementation(projects.coreDatabase)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
