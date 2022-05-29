package com.r2b.apps.xuser.data.api.marvel

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.nio.charset.Charset

class LoadResourcesTestRule(
    private val filename: String,
): TestWatcher() {

    lateinit var resource: String

    // setUp
    override fun starting(description: Description) {
        resource = loadJson(this::class.java.classLoader, filename)
        super.starting(description)
    }

    private fun loadJson(classLoader: ClassLoader, name: String): String =
        classLoader.getResource(name)?.readText(charset = Charset.forName("UTF8")) ?: ""

}
