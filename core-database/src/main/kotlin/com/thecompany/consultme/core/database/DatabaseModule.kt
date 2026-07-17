// Copyright 2026 MyCompany
package com.thecompany.consultme.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the Room database and its DAOs. Installed in [SingletonComponent] so
 * a single database instance is shared app-wide.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideExampleDatabase(@ApplicationContext context: Context): ExampleDatabase =
        Room.databaseBuilder(context, ExampleDatabase::class.java, "consultme.db")
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed placeholder rows so the example screen shows data on
                        // first launch. Delete this callback once you wire a real
                        // data source (network sync, user input, etc.).
                        db.execSQL(
                            "INSERT INTO example_items (label) VALUES " +
                                "('First example item'), " +
                                "('Second example item'), " +
                                "('Third example item')",
                        )
                    }
                },
            )
            .build()

    @Provides
    fun provideExampleItemDao(database: ExampleDatabase): ExampleItemDao = database.exampleItemDao()
}
