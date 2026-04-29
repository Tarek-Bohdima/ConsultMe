// Copyright 2025 MyCompany
plugins {
    id("consultme.android.library")
    id("consultme.android.hilt")
}

android {
    namespace = "com.thecompany.consultme.core.database"

    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(project(":core-testing"))
    androidTestImplementation(project(":core-testing"))
}
