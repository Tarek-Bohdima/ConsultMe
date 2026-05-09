// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.compose")
}

android {
    namespace = "com.thecompany.consultme.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
