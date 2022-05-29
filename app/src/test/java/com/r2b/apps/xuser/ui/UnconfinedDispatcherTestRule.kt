package com.r2b.apps.xuser.ui

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class UnconfinedDispatcherTestRule: TestWatcher() {

    //private val unconfinedDispatcher: CoroutineDispatcher

    override fun starting(description: Description) {
        //Dispatchers.setMain(unconfinedDispatcher)
        super.starting(description)
    }

    override fun finished(description: Description) {
        //Dispatchers.resetMain()
        super.finished(description)
    }
}
