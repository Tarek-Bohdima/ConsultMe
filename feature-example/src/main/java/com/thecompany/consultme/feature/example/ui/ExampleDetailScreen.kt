// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thecompany.consultme.core.model.ExampleItem

/**
 * Stateful detail entry point. Obtains the [ExampleDetailViewModel] via Hilt's
 * assisted-factory overload, passing the navigated-to `itemId` through
 * `creationCallback` — the Nav3 recipe for feeding a route argument into a
 * ViewModel. `:app` calls this from the `ExampleDetailRoute` entry.
 */
@Composable
fun ExampleDetailScreen(itemId: Long, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<ExampleDetailViewModel, ExampleDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(itemId) },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ExampleDetailScreen(uiState = uiState, onBack = onBack, modifier = modifier)
}

/** Stateless overload — used by previews and Compose UI tests. */
@Composable
fun ExampleDetailScreen(uiState: ExampleDetailUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = when (uiState) {
                ExampleDetailUiState.Loading -> "Loading…"
                ExampleDetailUiState.NotFound -> "Item not found."
                is ExampleDetailUiState.Loaded -> uiState.item.label
            },
        )
        Button(onClick = onBack) { Text(text = "Back") }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleDetailLoadedPreview() {
    ExampleDetailScreen(
        uiState = ExampleDetailUiState.Loaded(ExampleItem(1, "First example item")),
        onBack = {},
    )
}
