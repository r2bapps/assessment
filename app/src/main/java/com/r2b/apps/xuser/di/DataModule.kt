package com.r2b.apps.xuser.di

import com.r2b.apps.xuser.data.UserRepositoryImpl
import com.r2b.apps.xuser.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl,
    ) : UserRepository

}