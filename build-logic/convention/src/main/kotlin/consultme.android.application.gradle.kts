// Copyright 2026 MyCompany
import com.android.build.api.dsl.ApplicationExtension
import com.thecompany.consultme.buildlogic.configureBuildFeatures
import com.thecompany.consultme.buildlogic.configureKotlinAndroid
import com.thecompany.consultme.buildlogic.configureLint
import com.thecompany.consultme.buildlogic.configureManagedDevices

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("consultme.kover")
}

extensions.configure<ApplicationExtension> {
    configureKotlinAndroid(this)
    defaultConfig {
        targetSdk = 36
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
