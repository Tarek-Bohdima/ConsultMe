// Copyright 2026 MyCompany
package com.thecompany.consultme.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Room DAO for [ExampleItemEntity]. */
@Dao
interface ExampleItemDao {

    /**
     * Observes all example items ordered by id. Returning a [Flow] makes the
     * query reactive — Room re-emits whenever the `example_items` table changes.
     */
    @Query("SELECT * FROM example_items ORDER BY id")
    fun getExampleItems(): Flow<List<ExampleItemEntity>>

    @Insert
    suspend fun insertAll(items: List<ExampleItemEntity>)
}
