// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example

import com.thecompany.consultme.core.domain.ExampleRepository
import com.thecompany.consultme.core.model.ExampleItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Test-only binding for [ExampleRepository].
 *
 * Feature modules depend on the domain *port* but not on `:core-data` (the
 * adapter), so a `@HiltAndroidTest` graph here has no repository implementation
 * to satisfy `ExampleViewModel`. This module supplies a deterministic fake,
 * keeping instrumented tests fast and free of Room/`:core-data`. Real feature
 * tests swap the returned items for whatever the scenario needs.
 */
@Module
@InstallIn(SingletonComponent::class)
object FakeDataModule {

    @Provides
    @Singleton
    fun provideExampleRepository(): ExampleRepository = object : ExampleRepository {
        override fun getExampleItems(): Flow<List<ExampleItem>> = flowOf(emptyList())
    }
}
