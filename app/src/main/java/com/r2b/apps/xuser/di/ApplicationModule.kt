package com.r2b.apps.xuser.di

import com.r2b.apps.xuser.di.qualifier.BackgroundScope
import com.r2b.apps.xuser.di.qualifier.MainScope
import com.r2b.apps.xuser.di.qualifier.Pagination
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    // TODO: Extract to config file
    private val pageItems: Int = 20

    @MainScope
    @Singleton
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @BackgroundScope
    @Singleton
    @Provides
    fun provideBackgroundDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Pagination
    @Provides
    fun providePageItems(): Int = pageItems

}