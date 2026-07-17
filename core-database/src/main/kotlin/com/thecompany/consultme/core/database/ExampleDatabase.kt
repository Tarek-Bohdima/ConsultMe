// Copyright 2026 MyCompany
package com.thecompany.consultme.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The app's Room database. Add new entities to `entities` and bump `version`
 * (with a migration) as the schema grows. Schemas are exported to
 * `core-database/schemas/` — the `consultme.android.room` convention sets
 * `room.schemaLocation`, and those JSON files should be committed so Room can
 * verify migrations.
 */
@Database(entities = [ExampleItemEntity::class], version = 1, exportSchema = true)
abstract class ExampleDatabase : RoomDatabase() {
    abstract fun exampleItemDao(): ExampleItemDao
}
