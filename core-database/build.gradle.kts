// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.hilt")
    id("consultme.android.room")
}

android {
    namespace = "com.thecompany.consultme.core.database"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    testImplementation(projects.coreTesting)
    androidTestImplementation(projects.coreTesting)
}
