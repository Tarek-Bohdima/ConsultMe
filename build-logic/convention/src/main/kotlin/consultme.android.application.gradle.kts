// Copyright 2026 MyCompany
import com.android.build.api.dsl.ApplicationExtension
import com.thecompany.consultme.buildlogic.configureBuildFeatures
import com.thecompany.consultme.buildlogic.configureKotlinAndroid
import com.thecompany.consultme.buildlogic.configureLint
import com.thecompany.consultme.buildlogic.configureManagedDevices

plugins {
    // AGP 9+ bundles Kotlin support — no `org.jetbrains.kotlin.android` plugin
    // applied here. See https://kotl.in/gradle/agp-built-in-kotlin.
    id("com.android.application")
    id("consultme.kover")
}

// Consumer side of the baseline-profile pipeline. Deferred via
// `pluginManager.withPlugin` so it only applies *after* the Android
// application extension is fully wired — eager apply in the `plugins { }`
// block above triggers `Module :: is not a supported android module`
// during AGP 9's precompiled-plugin accessor generation phase.
pluginManager.withPlugin("com.android.application") {
    pluginManager.apply("androidx.baselineprofile")
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
