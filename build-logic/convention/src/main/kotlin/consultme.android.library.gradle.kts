// Copyright 2026 MyCompany
import com.android.build.api.dsl.LibraryExtension
import com.thecompany.consultme.buildlogic.configureBuildFeatures
import com.thecompany.consultme.buildlogic.configureKotlinAndroid
import com.thecompany.consultme.buildlogic.configureLint
import com.thecompany.consultme.buildlogic.configureManagedDevices

plugins {
    // AGP 9+ bundles Kotlin support — `org.jetbrains.kotlin.android` removed.
    id("com.android.library")
    id("consultme.kover")
}

extensions.configure<LibraryExtension> {
    configureKotlinAndroid(this)
    defaultConfig {
        testInstrumentationRunner = "com.thecompany.consultme.core.testing.HiltTestRunner"
    }
    // AGP 9 turned `android.proguard.failOnMissingFiles` to `true` by default.
    // The previous library convention referenced `consumer-rules.pro` and
    // `proguard-rules.pro` per module, even when those files didn't exist —
    // AGP 8 silently ignored them, AGP 9 fails the build. Library modules
    // can opt back in by declaring `consumerProguardFiles(...)` /
    // `proguardFiles(...)` in their own build script when they ship rules.
    configureBuildFeatures()
    configureLint(this)
    configureManagedDevices()
}
