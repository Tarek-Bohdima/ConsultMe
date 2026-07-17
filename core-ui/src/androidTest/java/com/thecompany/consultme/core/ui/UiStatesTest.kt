// Copyright 2026 MyCompany
package com.thecompany.consultme.core.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Compose UI tests for the shared `:core-ui` state containers. */
@RunWith(AndroidJUnit4::class)
class UiStatesTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsMessage() {
        composeTestRule.setContent { EmptyState(message = "Nothing here yet") }
        composeTestRule.onNodeWithText("Nothing here yet").assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetry_andForwardsClick_whenCallbackProvided() {
        var retried = false
        composeTestRule.setContent { ErrorState(message = "Something broke", onRetry = { retried = true }) }

        composeTestRule.onNodeWithText("Something broke").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed().performClick()
        composeTestRule.runOnIdle { assertThat(retried).isTrue() }
    }
}
