// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thecompany.consultme.core.model.ExampleItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Demonstrates a Compose UI test against the stateless screen overload — the
 * stateful entry point is harder to drive in tests because it pulls a
 * `hiltViewModel()`. Drive UI through state and verify rendering.
 */
@RunWith(AndroidJUnit4::class)
class ExampleScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test fun screen_rendersItemLabels() {
        composeTestRule.setContent {
            ExampleScreen(
                uiState = ExampleUiState.Success(
                    items = listOf(ExampleItem(1, "First example item")),
                ),
                onItemClick = {},
            )
        }
        composeTestRule.onNodeWithText("First example item").assertIsDisplayed()
    }

    @Test fun screen_rendersEmptyState() {
        composeTestRule.setContent {
            ExampleScreen(uiState = ExampleUiState.Empty, onItemClick = {})
        }
        composeTestRule
            .onNodeWithText("No items yet — replace this screen with your feature.")
            .assertIsDisplayed()
    }
}
