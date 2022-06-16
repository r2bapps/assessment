package com.r2b.apps.lib.api.randomuser

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockWebServerTestRule(
    private val mockWebServer: MockWebServer,
): TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                setUp()
                try {
                    base.evaluate()
                } finally {
                    tearDown()
                }
            }
        }
    }

    private fun setUp() {
        mockWebServer.start()
    }

    private fun tearDown() {
        mockWebServer.shutdown()
    }

}