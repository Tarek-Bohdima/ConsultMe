// Copyright 2026 MyCompany
package com.thecompany.consultme.core.data

import com.thecompany.consultme.core.domain.ExampleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds data-layer ports to their implementations. `@Binds` is the cheapest way
 * to tell Hilt "when something needs [ExampleRepository], give it
 * [DefaultExampleRepository]" — no factory method body, just the type mapping.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindExampleRepository(impl: DefaultExampleRepository): ExampleRepository
}
