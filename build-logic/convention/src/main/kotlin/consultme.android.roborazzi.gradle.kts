// Copyright 2026 MyCompany
import com.thecompany.consultme.buildlogic.configureUnitTests
import org.gradle.accessors.dm.LibrariesForLibs

// Roborazzi JVM snapshot-test wiring. Apply to a module that already applies
// `consultme.android.feature` (or library + compose) and you get:
//   - the `io.github.takahirom.roborazzi` Gradle plugin (record/verify tasks)
//   - roborazzi + roborazzi-compose + robolectric on testImplementation
//   - testOptions.unitTests.isIncludeAndroidResources = true, which is
//     required for stringResource(R.string.…) to resolve in JVM tests
//
// Workflow:
//   ./gradlew :feature-foo:recordRoborazziDebug   # generate baselines
//   ./gradlew :feature-foo:verifyRoborazziDebug   # CI gate against baselines
//
// AGP 9 compatibility starts at roborazzi 1.56.0 (older releases reference the
// now-removed TestedExtension). Pinned in gradle/libs.versions.toml.

plugins {
    id("io.github.takahirom.roborazzi")
}

val libs = the<LibrariesForLibs>()

configureUnitTests()

dependencies {
    // The Compose BOM aligns the test-side compose deps with main-set versions.
    "testImplementation"(platform(libs.androidx.compose.bom))
    // createComposeRule + onRoot — used by every screenshot test.
    "testImplementation"(libs.androidx.compose.ui.test.junit4)
    "testImplementation"(libs.roborazzi)
    "testImplementation"(libs.roborazzi.compose)
    "testImplementation"(libs.robolectric)
}
