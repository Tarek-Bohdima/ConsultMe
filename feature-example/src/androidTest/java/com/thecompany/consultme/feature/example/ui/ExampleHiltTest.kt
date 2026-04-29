// Copyright 2026 MyCompany
package com.thecompany.consultme.feature.example.ui

import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.thecompany.consultme.core.testing.HiltTestRule
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Demonstrates a Hilt-aware instrumented test using `:core-testing`'s
 * [HiltTestRule]. The `HiltTestRunner` (wired by the convention plugins) swaps
 * the application class for `HiltTestApplication` so field injection works.
 */
@HiltAndroidTest
class ExampleHiltTest {

    @get:Rule val hiltTestRule = HiltTestRule(this)

    @Inject @ApplicationContext
    lateinit var context: Context

    @Before fun setUp() {
        hiltTestRule.inject()
    }

    @Test fun hiltGraph_providesApplicationContext() {
        assertThat(context.applicationContext).isNotNull()
    }
}
