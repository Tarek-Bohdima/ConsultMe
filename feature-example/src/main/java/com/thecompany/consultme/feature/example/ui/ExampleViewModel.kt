// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecompany.consultme.core.domain.GetExampleItemsUseCase
import com.thecompany.consultme.core.model.ExampleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * A worked example of the template's presentation conventions: a
 * `@HiltViewModel` that consumes a domain use-case and exposes an immutable
 * [StateFlow] of UI state. The ViewModel never touches Room or the repository
 * impl directly — it depends only on [GetExampleItemsUseCase], which depends
 * only on the repository port. Replace this with your real screen state.
 */
@HiltViewModel
class ExampleViewModel @Inject constructor(getExampleItems: GetExampleItemsUseCase) : ViewModel() {

    val uiState: StateFlow<ExampleUiState> =
        getExampleItems()
            .map { items ->
                if (items.isEmpty()) ExampleUiState.Empty else ExampleUiState.Success(items)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = ExampleUiState.Loading,
            )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

/** UI state for the example screen. */
sealed interface ExampleUiState {
    /** Items are still loading from the data layer. */
    data object Loading : ExampleUiState

    /** Loaded successfully, but there are no items to show. */
    data object Empty : ExampleUiState

    /** Loaded successfully with one or more items. */
    data class Success(val items: List<ExampleItem>) : ExampleUiState
}
