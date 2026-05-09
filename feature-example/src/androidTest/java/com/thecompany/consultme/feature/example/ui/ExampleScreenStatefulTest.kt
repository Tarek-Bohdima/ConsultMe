// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Demonstrates the stateful-screen test pattern: pass a real
 * [ExampleViewModel] instance via the screen's `viewModel = ...` parameter
 * (avoiding the `hiltViewModel()` default), drive state by calling the
 * ViewModel's public API, and observe the screen recompose.
 *
 * This complements [ExampleScreenTest], which tests the stateless overload
 * directly (the simpler path). Use this pattern when you want to verify
 * the round-trip — UI event → ViewModel → state change → recomposition —
 * without standing up a full Hilt graph.
 *
 * For tests that need to mock a Repository or other ViewModel
 * dependencies, prefer the same shape: construct the ViewModel with fakes
 * passed to its constructor, then proceed exactly as below. MockK is on
 * the classpath via `:core-testing` if you need it; this template just
 * doesn't need it for the placeholder feature.
 */
@RunWith(AndroidJUnit4::class)
class ExampleScreenStatefulTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun screen_reactsToViewModelStateAndForwardsClicks() {
        val viewModel = ExampleViewModel()

        composeTestRule.setContent {
            ExampleScreen(viewModel = viewModel)
        }

        // Idle state renders the placeholder label.
        composeTestRule
            .onNodeWithText("Replace this screen with your feature.")
            .assertIsDisplayed()

        // Tap the button — the stateful screen forwards to viewModel.onClicked().
        composeTestRule.onNodeWithText("Click me").performClick()

        // The click flips the VM's StateFlow; the screen recomposes against the new state.
        composeTestRule.onNodeWithText("Clicked!").assertIsDisplayed()
    }
}
