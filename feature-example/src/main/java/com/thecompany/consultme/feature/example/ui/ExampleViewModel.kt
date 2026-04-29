// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Placeholder ViewModel that doubles as a worked example of the template's
 * Hilt + StateFlow conventions. Replace with your real screen state.
 */
@HiltViewModel
class ExampleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ExampleUiState>(ExampleUiState.Idle)
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    fun onClicked() {
        _uiState.value = ExampleUiState.Clicked
    }
}

sealed interface ExampleUiState {
    data object Idle : ExampleUiState
    data object Clicked : ExampleUiState
}
