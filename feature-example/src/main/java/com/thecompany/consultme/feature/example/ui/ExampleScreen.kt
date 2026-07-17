// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thecompany.consultme.core.model.ExampleItem
import com.thecompany.consultme.core.ui.EmptyState
import com.thecompany.consultme.core.ui.LoadingState

/**
 * Stateful entry point — pulls the ViewModel via Hilt and forwards state to
 * the stateless overload below. Callers (the host activity, navigation graphs)
 * should depend on this version.
 */
@Composable
fun ExampleScreen(
    modifier: Modifier = Modifier,
    onItemClick: (ExampleItem) -> Unit = {},
    viewModel: ExampleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ExampleScreen(uiState = uiState, onItemClick = onItemClick, modifier = modifier)
}

/**
 * Stateless overload — used by previews and Compose UI tests so they don't
 * need a ViewModel at construction. Loading/empty states reuse the shared
 * containers from `:core-ui`.
 */
@Composable
fun ExampleScreen(uiState: ExampleUiState, onItemClick: (ExampleItem) -> Unit, modifier: Modifier = Modifier) {
    when (uiState) {
        ExampleUiState.Loading -> LoadingState(modifier)

        ExampleUiState.Empty ->
            EmptyState(message = "No items yet — replace this screen with your feature.", modifier = modifier)

        is ExampleUiState.Success -> LazyColumn(modifier = modifier.fillMaxSize()) {
            items(items = uiState.items, key = { it.id }) { item ->
                Text(
                    text = item.label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(16.dp),
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenSuccessPreview() {
    Column(verticalArrangement = Arrangement.Center) {
        ExampleScreen(
            uiState = ExampleUiState.Success(
                items = listOf(
                    ExampleItem(1, "First example item"),
                    ExampleItem(2, "Second example item"),
                ),
            ),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenEmptyPreview() {
    ExampleScreen(uiState = ExampleUiState.Empty, onItemClick = {})
}
