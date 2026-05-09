// Copyright 2025 MyCompany
import com.thecompany.consultme.buildlogic.ModuleGraphTask
import org.gradle.api.artifacts.ProjectDependency

// Configurations whose declared `project(...)` deps belong on the graph.
// Variant-specific (`releaseImplementation`, `debugImplementation`) and
// test-only ones are intentionally excluded — they clutter the picture
// without adding signal for adopters reading the file.
val graphableConfigurations = setOf("api", "implementation", "compileOnly", "runtimeOnly")

// Collected lazily after every subproject has finished evaluating, so we
// don't fight other plugins' `afterEvaluate` registrations.
val collectedModuleDeps: MutableMap<String, Set<String>> = mutableMapOf()

gradle.projectsEvaluated {
    subprojects.forEach { sub ->
        if (!sub.buildFile.exists()) return@forEach
        collectedModuleDeps[sub.path] = sub.configurations
            .filter { conf -> conf.name in graphableConfigurations }
            .flatMap { conf -> conf.dependencies.filterIsInstance<ProjectDependency>() }
            .map { dep -> dep.path }
            .toSet()
    }
}

tasks.register<ModuleGraphTask>("moduleGraph") {
    group = "documentation"
    description = "Generate the Mermaid module-dependency graph at docs/MODULE_GRAPH.md"
    moduleDependencies.set(provider { collectedModuleDeps.toMap() })
    outputFile.set(layout.projectDirectory.file("docs/MODULE_GRAPH.md"))
    // Cross-project access at config time prevents configuration-cache reuse.
    // Acceptable for a documentation task that's run on demand, not per-build.
    notCompatibleWithConfigurationCache("Reads subproject configurations to collect inter-module deps")
}
