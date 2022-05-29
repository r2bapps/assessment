package com.r2b.apps.xuser.di

import com.r2b.apps.xuser.di.qualifier.Pagination
import com.r2b.apps.xuser.di.qualifier.WSRetry
import com.r2b.apps.xuser.domain.di.qualifier.BackgroundScope
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
    private val wsRetry: Boolean = true

    @BackgroundScope
    @Singleton
    @Provides
    fun provideBackgroundDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Pagination
    @Provides
    fun providePageItems(): Int = pageItems

    @WSRetry
    @Provides
    fun provideWSRetry(): Boolean = wsRetry

}