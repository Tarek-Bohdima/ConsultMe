// Copyright 2026 MyCompany
package com.thecompany.consultme.buildlogic

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = 36
        defaultConfig.minSdk = 26
        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    // AGP 9 + built-in Kotlin: there's no `KotlinAndroidProjectExtension`
    // registered at the project level anymore. Java toolchain is set via
    // the Java extension (Kotlin inherits from it); the surviving Kotlin-
    // specific knob (`-Xcontext-parameters`) goes on KotlinCompile tasks.
    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}

internal fun CommonExtension.configureBuildFeatures() {
    // `compose` is managed by the dedicated `consultme.android.compose` plugin
    // so that applying it before/after this convention is order-independent.
    buildFeatures.aidl = false
    buildFeatures.buildConfig = false
    buildFeatures.renderScript = false
    buildFeatures.shaders = false
}

/**
 * Registers a Gradle Managed Device so CI can run `connectedCheck` without a
 * physical emulator. Device name picks the AGP task slug
 * (`pixel6api30DebugAndroidTest`); `aosp-atd` is the slimmed-down Android Test
 * Device system image — fastest boot, no Google Play services.
 */
internal fun CommonExtension.configureManagedDevices() {
    testOptions.managedDevices.allDevices.create<ManagedVirtualDevice>("pixel6api30") {
        device = "Pixel 6"
        apiLevel = 30
        systemImageSource = "aosp-atd"
    }
}

internal fun Project.configureLint(commonExtension: CommonExtension) {
    // AGP 9 turned `lint` from a block helper on `CommonExtension` into a
    // property of type `Lint`. Most former toggles (textReport / htmlReport /
    // checkAllWarnings / abortOnError / checkDependencies, etc.) were also
    // dropped — most are defaults now; the rest live on individual lint tasks.
    commonExtension.lint.apply {
        baseline = file("lint-baseline.xml")
        quiet = true
    }
}
