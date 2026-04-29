// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thecompany.consultme.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Demonstrates the template's flavor of ViewModel unit test:
 * [MainDispatcherRule] swaps the main dispatcher; Turbine asserts on
 * `StateFlow` emissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun uiState_emitsIdleThenClicked() = runTest {
        val viewModel = ExampleViewModel()
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ExampleUiState.Idle)
            viewModel.onClicked()
            assertThat(awaitItem()).isEqualTo(ExampleUiState.Clicked)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
