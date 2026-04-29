// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.hilt")
}

android {
    namespace = "com.thecompany.consultme.core.data"
}

dependencies {
    implementation(projects.coreDatabase)

    implementation(libs.androidx.core.ktx)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
