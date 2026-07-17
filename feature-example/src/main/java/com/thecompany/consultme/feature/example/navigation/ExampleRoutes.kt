// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation 3 route keys for this feature.
 *
 * Each key is a `@Serializable` [NavKey] so Nav3 can persist and restore the
 * back stack across configuration changes and process death. Features own their
 * route types; `:app` composes them into a single `NavDisplay`. Arguments travel
 * as constructor properties of the key (e.g. [ExampleDetailRoute.id]).
 */
@Serializable
data object ExampleListRoute : NavKey

@Serializable
data class ExampleDetailRoute(val id: Long) : NavKey
