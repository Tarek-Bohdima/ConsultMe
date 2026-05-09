// Copyright 2025 MyCompany
package com.thecompany.consultme.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the baseline profile that ships with `:app`'s release APK.
 *
 * Run via `./gradlew :app:generateReleaseBaselineProfile` (uses the GMD
 * configured in [baselineprofile/build.gradle.kts]). The generated file
 * lands at `app/src/main/baseline-prof.txt` and is committed to the repo.
 *
 * Adopters: extend the [collect] block with the screens that matter for
 * your app's perceived startup. The default just covers cold launch +
 * landing on the first screen — which is enough to win 15–30% on first-
 * frame time.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(packageName = TARGET_PACKAGE) {
        pressHome()
        startActivityAndWait()
        // Adopter extension point: navigate through critical-path screens
        // before this lambda returns. Anything reachable here gets compiled
        // ahead-of-time on first install.
    }

    private companion object {
        // Tracks `:app`'s applicationId. Adopters: keep this in lockstep
        // when renaming via `scripts/rename-template.py`.
        const val TARGET_PACKAGE = "com.thecompany.consultme"
    }
}
