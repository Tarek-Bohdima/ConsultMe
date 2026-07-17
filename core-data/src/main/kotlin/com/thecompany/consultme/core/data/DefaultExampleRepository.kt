// Copyright 2026 MyCompany
package com.thecompany.consultme.core.data

import com.thecompany.consultme.core.database.ExampleItemDao
import com.thecompany.consultme.core.database.ExampleItemEntity
import com.thecompany.consultme.core.domain.ExampleRepository
import com.thecompany.consultme.core.model.ExampleItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed adapter for [ExampleRepository]. Reads entities from the DAO and
 * maps them onto domain models, so nothing downstream sees a storage type.
 * Bound to the [ExampleRepository] port by [DataModule].
 */
class DefaultExampleRepository @Inject constructor(private val exampleItemDao: ExampleItemDao) : ExampleRepository {

    override fun getExampleItems(): Flow<List<ExampleItem>> = exampleItemDao.getExampleItems().map { entities ->
        entities.map(ExampleItemEntity::asExternalModel)
    }

    override fun getExampleItem(id: Long): Flow<ExampleItem?> =
        exampleItemDao.getExampleItem(id).map { entity -> entity?.asExternalModel() }
}
