// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecompany.consultme.core.domain.GetExampleItemUseCase
import com.thecompany.consultme.core.model.ExampleItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Detail ViewModel for a single example item.
 *
 * Demonstrates the idiomatic Nav3 + Hilt recipe for passing a navigation
 * argument to a ViewModel: the route id is **assisted-injected** (`@Assisted`)
 * and the rest of the graph (`GetExampleItemUseCase`) comes from Hilt. The
 * caller supplies the id through [Factory] via `hiltViewModel(creationCallback)`.
 */
@HiltViewModel(assistedFactory = ExampleDetailViewModel.Factory::class)
class ExampleDetailViewModel @AssistedInject constructor(
    @Assisted private val itemId: Long,
    getExampleItem: GetExampleItemUseCase,
) : ViewModel() {

    val uiState: StateFlow<ExampleDetailUiState> =
        getExampleItem(itemId)
            .map { item ->
                if (item == null) ExampleDetailUiState.NotFound else ExampleDetailUiState.Loaded(item)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = ExampleDetailUiState.Loading,
            )

    @AssistedFactory
    interface Factory {
        fun create(itemId: Long): ExampleDetailViewModel
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

/** UI state for the example detail screen. */
sealed interface ExampleDetailUiState {
    data object Loading : ExampleDetailUiState
    data object NotFound : ExampleDetailUiState
    data class Loaded(val item: ExampleItem) : ExampleDetailUiState
}
