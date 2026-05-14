// Copyright 2025 MyCompany
package com.thecompany.consultme.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke benchmark that doubles as a regression guard for startup time.
 *
 * Two variants:
 * - `startupCompilationNone`: cold-start with no AOT compilation. Worst
 *   case — the number a brand-new install hits.
 * - `startupCompilationBaselineProfile`: cold-start with the committed
 *   baseline profile applied. The delta vs `startupCompilationNone` is
 *   the win adopters get for free.
 *
 * Run via `./gradlew :baselineprofile:pixel6api30BenchmarkAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmarks {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() = startup(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfile() = startup(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun startup(mode: CompilationMode) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = mode,
        startupMode = StartupMode.COLD,
        iterations = 10,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
    }

    private companion object {
        // Must match the applicationId of the `:app` variant being benchmarked.
        // Adopters who add product flavors must update this per flavor —
        // macrobenchmark targets a single package, and the defaultConfig value
        // is never what ships when any flavor sets its own applicationId.
        // See `baselineprofile/README.md`.
        const val TARGET_PACKAGE = "com.thecompany.consultme"
    }
}
