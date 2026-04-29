// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Stateful entry point — pulls the ViewModel via Hilt and forwards state to
 * the stateless overload below. Callers (the host activity, navigation graphs)
 * should depend on this version.
 */
@Composable
fun ExampleScreen(modifier: Modifier = Modifier, viewModel: ExampleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ExampleScreen(
        uiState = uiState,
        onClick = viewModel::onClicked,
        modifier = modifier,
    )
}

/**
 * Stateless overload — used by previews and Compose UI tests so they don't
 * need a ViewModel at construction.
 */
@Composable
fun ExampleScreen(uiState: ExampleUiState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = uiState.label())
        Button(onClick = onClick) {
            Text(text = "Click me")
        }
    }
}

private fun ExampleUiState.label(): String = when (this) {
    ExampleUiState.Idle -> "Replace this screen with your feature."
    ExampleUiState.Clicked -> "Clicked!"
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenPreview() {
    ExampleScreen(uiState = ExampleUiState.Idle, onClick = {})
}
