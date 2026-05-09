// Copyright 2025 MyCompany
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension

plugins {
    id("org.jetbrains.kotlinx.kover")
}

extensions.configure<KoverProjectExtension> {
    reports {
        filters {
            excludes {
                // Generated code is not meaningful coverage signal.
                classes(
                    // Hilt
                    "*_HiltModules*",
                    "*_GeneratedInjector*",
                    "*_Factory",
                    "*_Factory\$*",
                    "hilt_aggregated_deps*",
                    "Dagger*Component*",
                    "*_MembersInjector",
                    // Room (KSP)
                    "*_Impl",
                    "*_Impl\$*",
                    // Compose
                    "ComposableSingletons*",
                    // Generated arg/nav classes
                    "*\$\$serializer",
                )
                annotatedBy("dagger.internal.DaggerGenerated")
            }
        }
    }
}
