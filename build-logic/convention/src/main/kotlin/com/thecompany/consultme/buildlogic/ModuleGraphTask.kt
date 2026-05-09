// Copyright 2025 MyCompany
package com.thecompany.consultme.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Emits a [ModuleGraph] as text to [outputFile].
 *
 * Configuration-cache friendly: all data is captured up-front into
 * [moduleDependencies] (a `MapProperty<String, Set<String>>`) so the task
 * action never reaches into the Gradle `Project` model at execution time.
 *
 * The renderer ([ModuleGraphRenderer]) is currently hard-wired to
 * [MermaidModuleGraphRenderer]; adopters can extend the Strategy by adding
 * a new renderer class and a sibling task that selects it.
 */
abstract class ModuleGraphTask : DefaultTask() {
    @get:Input
    abstract val moduleDependencies: MapProperty<String, Set<String>>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val depsByPath = moduleDependencies.get()
        val nodes = depsByPath.keys.map(::ModuleNode)
        val edges = depsByPath.flatMap { (from, targets) ->
            targets.map { target -> ModuleEdge(ModuleNode(from), ModuleNode(target)) }
        }
        val graph = ModuleGraph(nodes, edges)
        val renderer: ModuleGraphRenderer = MermaidModuleGraphRenderer()
        outputFile.get().asFile.writeText(renderer.render(graph))
    }
}
