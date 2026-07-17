// Copyright 2026 MyCompany
package com.thecompany.consultme.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room storage type for an example item.
 *
 * Entities are an implementation detail of the data layer — they never leave
 * `:core-data`, which maps them onto the pure-Kotlin `ExampleItem` domain
 * model. Keeping storage and domain types separate lets the schema evolve
 * (columns, indices, migrations) without rippling into features.
 */
@Entity(tableName = "example_items")
data class ExampleItemEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val label: String)
