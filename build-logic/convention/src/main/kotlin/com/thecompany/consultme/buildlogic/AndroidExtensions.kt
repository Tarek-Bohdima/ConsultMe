// Copyright 2026 MyCompany
package com.thecompany.consultme.buildlogic

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = 36
        defaultConfig.minSdk = 25
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    extensions.configure<KotlinAndroidProjectExtension> {
        jvmToolchain(17)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.set(listOf("-Xcontext-receivers"))
        }
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.configureBuildFeatures() {
    // `compose` is managed by the dedicated `consultme.android.compose` plugin
    // so that applying it before/after this convention is order-independent.
    buildFeatures.apply {
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }
}

/**
 * Registers a Gradle Managed Device so CI can run `connectedCheck` without a
 * physical emulator. Device name picks the AGP task slug
 * (`pixel6api30DebugAndroidTest`); `aosp-atd` is the slimmed-down Android Test
 * Device system image — fastest boot, no Google Play services.
 */
internal fun CommonExtension<*, *, *, *, *, *>.configureManagedDevices() {
    testOptions {
        managedDevices {
            allDevices.create("pixel6api30", ManagedVirtualDevice::class.java).apply {
                device = "Pixel 6"
                apiLevel = 30
                systemImageSource = "aosp-atd"
            }
        }
    }
}

internal fun Project.configureLint(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.lint {
        baseline = file("lint-baseline.xml")
        quiet = true
        checkAllWarnings = true
        warningsAsErrors = false
        textReport = true
        htmlReport = true
        xmlReport = false
        checkReleaseBuilds = true
        abortOnError = true
        checkDependencies = true
    }
}
