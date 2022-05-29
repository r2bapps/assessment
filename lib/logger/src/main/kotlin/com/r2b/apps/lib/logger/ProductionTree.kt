package com.r2b.apps.lib.logger

import timber.log.Timber

class ProductionTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Priority.VERBOSE.value || priority == Priority.DEBUG.value) {
            return
        }
        // TODO: Timber to Firebase
    }
}