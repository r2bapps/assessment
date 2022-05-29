package com.r2b.apps.xuser.di

import android.content.Context
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.lib.imageloader.ImageLoaderDelegate
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.lib.logger.LoggerDelegate
import com.r2b.apps.lib.tracker.Tracker
import com.r2b.apps.lib.tracker.TrackerDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ThirdPartyModule {

    @Singleton
    @Provides
    fun provideLogger(): Logger = LoggerDelegate()

    @Singleton
    @Provides
    fun provideTracker(): Tracker = TrackerDelegate()

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext applicationContext: Context,
        client: OkHttpClient,
    ): ImageLoader = ImageLoaderDelegate(
        applicationContext,
        client,
    )

}