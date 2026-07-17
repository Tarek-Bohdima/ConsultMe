// Copyright 2026 MyCompany
package com.thecompany.consultme.core.model

/**
 * A single example item — the template's placeholder domain type.
 *
 * This is a pure-Kotlin model with no Android or persistence concerns: the
 * data layer maps its own storage types (e.g. Room entities) onto this class,
 * and the presentation layer only ever sees this. Replace it with your own
 * domain model(s) as you build out a real feature.
 */
data class ExampleItem(val id: Long, val label: String)
