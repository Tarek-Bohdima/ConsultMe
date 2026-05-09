// Copyright 2026 MyCompany
import com.android.build.api.dsl.TestExtension
import com.thecompany.consultme.buildlogic.configureBuildFeatures
import com.thecompany.consultme.buildlogic.configureKotlinAndroid
import com.thecompany.consultme.buildlogic.configureManagedDevices

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

extensions.configure<TestExtension> {
    configureKotlinAndroid(this)
    defaultConfig.targetSdk = 36
    configureBuildFeatures()
    // Macrobenchmark + baseline profile generation runs against a GMD; share
    // the same `pixel6api30` device the application/library conventions use
    // so adopters have one entry-point AVD to install/cache locally and in CI.
    configureManagedDevices()
}
