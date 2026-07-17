// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.thecompany.consultme.core.domain.ExampleRepository
import com.thecompany.consultme.core.domain.GetExampleItemsUseCase
import com.thecompany.consultme.core.model.ExampleItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Demonstrates the stateful-screen test pattern: build a real
 * [ExampleViewModel] from a fake use-case, pass it via the screen's
 * `viewModel = ...` parameter (avoiding the `hiltViewModel()` default), and
 * observe the round-trip — data layer → ViewModel state → recomposition — plus
 * click forwarding, without standing up a full Hilt graph.
 *
 * This complements [ExampleScreenTest], which tests the stateless overload
 * directly (the simpler path). For dependencies you'd rather mock than fake,
 * MockK is on the classpath via `:core-testing`.
 */
@RunWith(AndroidJUnit4::class)
class ExampleScreenStatefulTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun screen_rendersViewModelItems_andForwardsClicks() {
        val items = listOf(ExampleItem(1, "First example item"))
        val viewModel = ExampleViewModel(GetExampleItemsUseCase(FakeExampleRepository(items)))
        var clicked: ExampleItem? = null

        composeTestRule.setContent {
            ExampleScreen(viewModel = viewModel, onItemClick = { clicked = it })
        }

        // The VM's StateFlow drives the list; the item renders after recomposition.
        composeTestRule.onNodeWithText("First example item").assertIsDisplayed().performClick()

        // The click bubbles out through the screen's onItemClick callback.
        composeTestRule.runOnIdle { assertThat(clicked).isEqualTo(items.first()) }
    }
}

private class FakeExampleRepository(private val items: List<ExampleItem>) : ExampleRepository {
    override fun getExampleItems(): Flow<List<ExampleItem>> = flowOf(items)
}
