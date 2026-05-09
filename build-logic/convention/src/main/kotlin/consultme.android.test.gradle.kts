// Copyright 2026 MyCompany
import com.android.build.api.dsl.TestExtension
import com.thecompany.consultme.buildlogic.configureBuildFeatures
import com.thecompany.consultme.buildlogic.configureKotlinAndroid

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

extensions.configure<TestExtension> {
    configureKotlinAndroid(this)
    defaultConfig.targetSdk = 36
    configureBuildFeatures()
}
