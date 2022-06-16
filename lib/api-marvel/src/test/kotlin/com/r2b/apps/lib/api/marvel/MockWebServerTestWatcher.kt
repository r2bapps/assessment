package com.r2b.apps.lib.api.marvel

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockWebServerTestWatcher(
    public val mockWebServer: MockWebServer,
): TestWatcher() {

    // setUp
    override fun starting(description: Description) {
        mockWebServer.start()
        super.starting(description)
    }

    // tearDown
    override fun finished(description: Description) {
        mockWebServer.shutdown()
        super.finished(description)
    }

}