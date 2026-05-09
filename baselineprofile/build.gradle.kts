// Copyright 2025 MyCompany
plugins {
    id("consultme.android.baselineprofile")
}

android {
    namespace = "com.thecompany.consultme.baselineprofile"

    defaultConfig {
        // Macrobenchmarks require the AndroidX runner.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // The :app variant this profile targets.
    targetProjectPath = ":app"
}

// Generate the profile against the existing GMD registered in the convention
// plugin (`pixel6api30`, `aosp-atd`). Adopters with a different device
// preference can override here.
baselineProfile {
    useConnectedDevices = false
    managedDevices += "pixel6api30"
}
