package com.r2b.apps.xuser

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.r2b.apps.lib.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var logger: Logger

    override fun onCreate() {
        super.onCreate()
        logger.init(BuildConfig.DEBUG)
    }

}
