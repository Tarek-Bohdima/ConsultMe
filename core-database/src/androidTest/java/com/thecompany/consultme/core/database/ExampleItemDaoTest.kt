// Copyright 2026 MyCompany
package com.thecompany.consultme.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented DAO test against a real (in-memory) Room database — this is
 * where the `@Query` SQL and Room's code generation actually get exercised, so
 * it runs on a device rather than as a JVM unit test.
 */
@RunWith(AndroidJUnit4::class)
class ExampleItemDaoTest {

    private lateinit var database: ExampleDatabase
    private lateinit var dao: ExampleItemDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExampleDatabase::class.java,
        ).build()
        dao = database.exampleItemDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAll_thenQuery_returnsItemsOrderedById() = runTest {
        dao.insertAll(
            listOf(
                ExampleItemEntity(label = "First"),
                ExampleItemEntity(label = "Second"),
            ),
        )

        val labels = dao.getExampleItems().first().map { it.label }

        assertThat(labels).containsExactly("First", "Second").inOrder()
    }
}
