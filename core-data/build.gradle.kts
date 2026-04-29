// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.hilt")
}

android {
    namespace = "com.thecompany.consultme.core.data"
}

dependencies {
    implementation(project(":core-database"))

    implementation(libs.androidx.core.ktx)

    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))
}
