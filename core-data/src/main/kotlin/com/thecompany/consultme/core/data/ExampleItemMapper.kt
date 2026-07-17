// Copyright 2026 MyCompany
package com.thecompany.consultme.core.data

import com.thecompany.consultme.core.database.ExampleItemEntity
import com.thecompany.consultme.core.model.ExampleItem

/**
 * Maps a Room [ExampleItemEntity] onto the domain [ExampleItem]. Isolating the
 * translation here keeps storage types from leaking past the data layer.
 */
internal fun ExampleItemEntity.asExternalModel(): ExampleItem = ExampleItem(id = id, label = label)
