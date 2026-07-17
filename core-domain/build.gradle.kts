// Copyright 2025 MyCompany
plugins {
    id("consultme.jvm.library")
}

dependencies {
    // `api` — ExampleItem appears in this module's public API (repository /
    // use-case return types), so consumers (features) get the model type
    // transitively without redeclaring the dependency.
    api(projects.coreModel)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}
