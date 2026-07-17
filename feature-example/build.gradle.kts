// Copyright 2025 MyCompany
plugins {
    id("consultme.android.feature")
    id("consultme.android.roborazzi")
}

android {
    namespace = "com.thecompany.consultme.feature.example"
}

dependencies {
    // Features depend on the domain layer (use-cases + ports), never on the
    // data layer directly. The repository impl is bound in :core-data and wired
    // into the graph by :app. `api` because the public screen composables expose
    // domain types (e.g. ExampleItem in onItemClick), so consumers such as :app
    // get them transitively.
    api(projects.coreDomain)

    implementation(libs.androidx.core.ktx)
}
