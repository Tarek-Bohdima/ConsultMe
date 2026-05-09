// Copyright 2025 MyCompany
package com.thecompany.consultme.buildlogic

/**
 * Renderer-agnostic representation of the project's module dependency graph.
 *
 * Decoupling collection from rendering lets new output formats (Mermaid, DOT,
 * ASCII, JSON) plug in without touching graph traversal — a Strategy seam at
 * [ModuleGraphRenderer].
 */
data class ModuleGraph(
    val nodes: List<ModuleNode>,
    val edges: List<ModuleEdge>,
)

data class ModuleNode(val path: String) {
    val displayName: String get() = path.removePrefix(":")
}

data class ModuleEdge(val from: ModuleNode, val to: ModuleNode)
