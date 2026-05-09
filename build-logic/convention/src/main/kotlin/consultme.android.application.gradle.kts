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
    // Consumer side of the baseline-profile pipeline. Wiring up the plugin
    // is enough; the actual `baselineProfile(project(":baselineprofile"))`
    // dep is declared in :app's build.gradle.kts so adopters can swap or
    // remove the producer module without touching the convention.
    id("androidx.baselineprofile")
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
