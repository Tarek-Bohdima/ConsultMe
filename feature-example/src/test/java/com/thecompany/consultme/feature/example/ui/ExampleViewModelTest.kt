// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thecompany.consultme.core.domain.ExampleRepository
import com.thecompany.consultme.core.domain.GetExampleItemsUseCase
import com.thecompany.consultme.core.model.ExampleItem
import com.thecompany.consultme.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Demonstrates the template's flavor of ViewModel unit test:
 * [MainDispatcherRule] swaps the main dispatcher; Turbine asserts on
 * `StateFlow` emissions. The ViewModel's collaborator (the use-case) is built
 * on a hand-rolled [FakeExampleRepository] — the seam that lets you drive
 * screen state from tests without Room or Hilt. Swap in MockK
 * (on the classpath via `:core-testing`) if you prefer mocks to fakes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun uiState_startsLoading_thenEmitsSuccess() = runTest {
        val items = listOf(ExampleItem(1, "First"), ExampleItem(2, "Second"))
        val viewModel = ExampleViewModel(GetExampleItemsUseCase(FakeExampleRepository(items)))

        // Before anything collects, the StateFlow holds its `initialValue`. Once
        // a collector subscribes, `WhileSubscribed` starts the upstream and the
        // state resolves — the intermediate value is conflated by StateFlow, so
        // Turbine asserts on the resolved state.
        assertThat(viewModel.uiState.value).isEqualTo(ExampleUiState.Loading)
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ExampleUiState.Success(items))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_emitsEmpty_whenNoItems() = runTest {
        val viewModel = ExampleViewModel(GetExampleItemsUseCase(FakeExampleRepository(emptyList())))

        assertThat(viewModel.uiState.value).isEqualTo(ExampleUiState.Loading)
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ExampleUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeExampleRepository(private val items: List<ExampleItem>) : ExampleRepository {
    override fun getExampleItems(): Flow<List<ExampleItem>> = flowOf(items)
    override fun getExampleItem(id: Long): Flow<ExampleItem?> = flowOf(items.firstOrNull { it.id == id })
}
