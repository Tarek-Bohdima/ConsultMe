// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Worked example of a Roborazzi snapshot test. Renders [ExampleScreen] on a
 * Robolectric-backed virtual device, captures a PNG, and (in verify mode)
 * diffs it against the baseline under
 * `feature-example/src/test/snapshots/roborazzi/`.
 *
 * Workflow when adopting:
 *   ./gradlew :feature-example:recordRoborazziDebug   # generate baseline
 *   git add feature-example/src/test/snapshots/       # commit baseline
 *   ./gradlew :feature-example:verifyRoborazziDebug   # CI gate
 *
 * Kept @Ignore in the template because no baseline ships with the
 * placeholder — removing @Ignore on a clean checkout would make CI fail.
 * Adopters who keep this module past the first feature port should record
 * a baseline and remove the annotation.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ExampleScreenSnapshotTest {

    @get:Rule val composeRule = createComposeRule()

    @Test
    @Ignore("Remove after running ./gradlew :feature-example:recordRoborazziDebug")
    fun exampleScreen_idle_matchesBaseline() {
        composeRule.setContent {
            ExampleScreen(uiState = ExampleUiState.Idle, onClick = {})
        }
        composeRule.onRoot().captureRoboImage()
    }
}
