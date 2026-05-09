// Copyright 2025 MyCompany
package com.thecompany.consultme.buildlogic

/**
 * Strategy for serializing a [ModuleGraph] to text.
 *
 * Implementations describe a single output format. The convention plugin's
 * `:moduleGraph` task selects one implementation; adopters wanting DOT,
 * ASCII, or JSON output can drop in their own renderer without forking the
 * traversal logic.
 */
interface ModuleGraphRenderer {
    fun render(graph: ModuleGraph): String
}
