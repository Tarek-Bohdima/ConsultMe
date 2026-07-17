// Copyright 2026 MyCompany
package com.thecompany.consultme.core.domain

import com.thecompany.consultme.core.model.ExampleItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Returns the stream of example items.
 *
 * A deliberately thin use-case: it just delegates to the repository port. Real
 * use-cases are where cross-repository orchestration, filtering, sorting, or
 * business rules live — keeping that logic out of ViewModels. The `operator
 * fun invoke` lets callers treat the instance like a function
 * (`getExampleItems()`). Hilt constructs it via `@Inject` and supplies the
 * [ExampleRepository] binding from `:core-data`.
 */
class GetExampleItemsUseCase @Inject constructor(private val repository: ExampleRepository) {
    operator fun invoke(): Flow<List<ExampleItem>> = repository.getExampleItems()
}
