// Copyright 2026 MyCompany
package com.thecompany.consultme.core.domain

import com.thecompany.consultme.core.model.ExampleItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository **port** for example items.
 *
 * The interface lives in the pure-Kotlin domain layer so use-cases (and, in
 * turn, features) depend only on this abstraction — never on a concrete data
 * source. The **adapter** ([`DefaultExampleRepository`] in `:core-data`)
 * implements it against Room and is bound into the graph by Hilt. This is the
 * ports-and-adapters seam that keeps `:core-domain` free of Android/framework
 * dependencies.
 */
interface ExampleRepository {
    /** A stream of the example items, emitting a new list whenever they change. */
    fun getExampleItems(): Flow<List<ExampleItem>>
}
