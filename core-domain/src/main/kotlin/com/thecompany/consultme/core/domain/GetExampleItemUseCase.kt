// Copyright 2026 MyCompany
package com.thecompany.consultme.core.domain

import com.thecompany.consultme.core.model.ExampleItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Returns a stream of a single example item by id (`null` if it doesn't exist).
 * Used by the detail screen to load the item the user navigated to.
 */
class GetExampleItemUseCase @Inject constructor(private val repository: ExampleRepository) {
    operator fun invoke(id: Long): Flow<ExampleItem?> = repository.getExampleItem(id)
}
