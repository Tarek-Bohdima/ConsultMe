// Copyright 2026 MyCompany
package com.thecompany.consultme.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/*
 * Generic, stateless screen-state containers shared across features.
 *
 * These live in :core-ui (not :core-designsystem, which owns theme tokens, and
 * not any :feature-*, which owns screens) so every feature renders loading /
 * empty / error states the same way without re-implementing them. They're
 * deliberately Hilt-free and take only plain parameters. Style them against the
 * design-system theme at the call site or extend them here.
 */

/** Full-size centered progress indicator for a loading state. */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** Full-size centered message for a "loaded but nothing to show" state. */
@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, textAlign = TextAlign.Center)
    }
}

/**
 * Full-size centered error message with an optional retry action. Pass
 * [onRetry] to show a Retry button; omit it for a terminal error.
 */
@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = message, textAlign = TextAlign.Center)
        if (onRetry != null) {
            Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    EmptyState(message = "Nothing here yet.")
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    ErrorState(message = "Something went wrong.", onRetry = {})
}
