// Copyright 2026 MyCompany
import com.android.build.gradle.LibraryExtension
import com.thecompany.consultme.buildlogic.configureBuildFeatures
import com.thecompany.consultme.buildlogic.configureKotlinAndroid
import com.thecompany.consultme.buildlogic.configureLint
import com.thecompany.consultme.buildlogic.configureManagedDevices

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

extensions.configure<LibraryExtension> {
    configureKotlinAndroid(this)
    defaultConfig {
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    configureBuildFeatures()
    configureLint(this)
    configureManagedDevices()
}
