// Copyright 2026 MyCompany
package com.thecompany.consultme.core.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thecompany.consultme.core.database.ExampleItemDao
import com.thecompany.consultme.core.database.ExampleItemEntity
import com.thecompany.consultme.core.model.ExampleItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Data-layer unit test. The DAO is an interface, so it can be faked — no Room
 * or device needed to verify the repository's real job: mapping storage
 * entities onto domain models. (Room's own SQL is exercised by the
 * instrumented `ExampleItemDaoTest` in `:core-database`.)
 */
class DefaultExampleRepositoryTest {

    @Test
    fun getExampleItems_mapsEntitiesToDomainModels() = runTest {
        val dao = FakeExampleItemDao(
            listOf(ExampleItemEntity(1, "First"), ExampleItemEntity(2, "Second")),
        )
        val repository = DefaultExampleRepository(dao)

        repository.getExampleItems().test {
            assertThat(awaitItem()).containsExactly(
                ExampleItem(1, "First"),
                ExampleItem(2, "Second"),
            ).inOrder()
            awaitComplete()
        }
    }
}

private class FakeExampleItemDao(private val items: List<ExampleItemEntity>) : ExampleItemDao {
    override fun getExampleItems(): Flow<List<ExampleItemEntity>> = flowOf(items)
    override fun getExampleItem(id: Long): Flow<ExampleItemEntity?> = flowOf(items.firstOrNull { it.id == id })
    override suspend fun insertAll(items: List<ExampleItemEntity>) = Unit
}
