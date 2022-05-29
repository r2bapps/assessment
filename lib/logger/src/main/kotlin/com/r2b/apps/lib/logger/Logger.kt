package com.r2b.apps.lib.logger

import timber.log.Timber

interface Logger {
    fun init(debugMode: Boolean = false)
    // TODO: Debug only while ProductionTree is unimplemented
    fun d(message: String)
    fun d(throwable: Throwable)
}

enum class Priority(val value: Int) {
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6),
    ASSERT(7),
}

class LoggerDelegate: Logger {
    override fun init(debugMode: Boolean) {
        val plant = if (debugMode) Timber.DebugTree() else ProductionTree()
        Timber.plant(plant)
    }
    override fun d(message: String) = Timber.d(message)
    override fun d(throwable: Throwable) = Timber.d(throwable)
}