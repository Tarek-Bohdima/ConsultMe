// Copyright 2026 MyCompany
package com.thecompany.consultme.core.testing

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Composed JUnit rule for Hilt-aware tests: pairs [HiltAndroidRule] with
 * [MainDispatcherRule] so a `@HiltAndroidTest` with `viewModelScope` work
 * needs one rule, not two.
 *
 * Usage:
 * ```
 * @HiltAndroidTest
 * class MyTest {
 *     @get:Rule val rule = HiltTestRule(this)
 *     @Inject lateinit var something: Something
 *     @Before fun setUp() { rule.inject() }
 * }
 * ```
 */
class HiltTestRule(testInstance: Any) : TestRule {
    private val hiltAndroidRule = HiltAndroidRule(testInstance)
    private val mainDispatcherRule = MainDispatcherRule()
    private val chain: TestRule = RuleChain
        .outerRule(mainDispatcherRule)
        .around(hiltAndroidRule)

    override fun apply(base: Statement, description: Description): Statement = chain.apply(base, description)

    fun inject() = hiltAndroidRule.inject()
}
