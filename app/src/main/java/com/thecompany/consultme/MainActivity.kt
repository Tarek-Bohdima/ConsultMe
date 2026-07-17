// Copyright 2025 MyCompany
package com.thecompany.consultme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.thecompany.consultme.core.designsystem.theme.ConsultMeTheme
import com.thecompany.consultme.feature.example.navigation.ExampleDetailRoute
import com.thecompany.consultme.feature.example.navigation.ExampleListRoute
import com.thecompany.consultme.feature.example.ui.ExampleDetailScreen
import com.thecompany.consultme.feature.example.ui.ExampleScreen
import com.thecompany.consultme.feature.example.ui.ExampleUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConsultMeTheme {
                ConsultMeNavDisplay()
            }
        }
    }
}

/**
 * The app-owned Navigation 3 host. `:app` is the only module that knows about
 * every feature, so it holds the back stack and composes each feature's route
 * entries into a single [NavDisplay]. Features contribute their `NavKey`s and
 * screen composables; add new destinations by adding `entry<…>` blocks.
 */
@Composable
private fun ConsultMeNavDisplay() {
    val backStack = rememberNavBackStack(ExampleListRoute)
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<ExampleListRoute> {
                    ExampleScreen(onItemClick = { item -> backStack.add(ExampleDetailRoute(item.id)) })
                }
                entry<ExampleDetailRoute> { key ->
                    ExampleDetailScreen(itemId = key.id, onBack = { backStack.removeLastOrNull() })
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    ConsultMeTheme {
        ExampleScreen(uiState = ExampleUiState.Empty, onItemClick = {})
    }
}
