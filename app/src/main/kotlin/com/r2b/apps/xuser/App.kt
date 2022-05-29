package com.r2b.apps.xuser

import android.app.Application
import com.r2b.apps.lib.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var logger: Logger
    @Inject lateinit var strictModeDelegate: StrictModeDelegate

    override fun onCreate() {
        super.onCreate()
        strictModeDelegate.init(BuildConfig.DEBUG)
        logger.init(BuildConfig.DEBUG)
    }

}
