// Copyright 2025 MyCompany
plugins {
    id("consultme.android.feature")
}

android {
    namespace = "com.thecompany.consultme.feature.example"
}

dependencies {
    implementation(projects.coreData)

    implementation(libs.androidx.core.ktx)
}
