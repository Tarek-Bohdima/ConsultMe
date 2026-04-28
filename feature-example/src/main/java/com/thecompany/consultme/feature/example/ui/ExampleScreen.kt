// Copyright 2025 MyCompany
package com.thecompany.consultme.feature.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ExampleScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Replace this screen with your feature.")
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenPreview() {
    ExampleScreen()
}
