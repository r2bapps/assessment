package com.r2b.apps.xuser.di

import android.content.Context
import androidx.room.Room
import com.r2b.apps.xuser.data.storage.UserDAO
import com.r2b.apps.xuser.data.storage.room.CacheDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    // TODO: Extract to config file
    private val databaseName: String = "cache-database"

    @Singleton
    @Provides
    fun provideCacheDatabase(
        @ApplicationContext applicationContext: Context,
    ): CacheDatabase =
        Room.databaseBuilder(
            applicationContext,
            CacheDatabase::class.java,
            databaseName,
        ).build()

    @Provides
    fun provideUserDAO(
        cacheDatabase: CacheDatabase,
    ): UserDAO =
        cacheDatabase.userDao()

}