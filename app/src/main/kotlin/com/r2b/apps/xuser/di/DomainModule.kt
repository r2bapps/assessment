package com.r2b.apps.xuser.di

import com.r2b.apps.xuser.di.qualifier.BackgroundScope
import com.r2b.apps.xuser.domain.repository.UserRepository
import com.r2b.apps.xuser.domain.usecase.*
import com.r2b.apps.xuser.domain.usecase.currentuser.GetCurrentUserUseCase
import com.r2b.apps.xuser.domain.usecase.currentuser.SetCurrentUserUseCase
import com.r2b.apps.xuser.domain.usecase.filter.FilterUsersUseCase
import com.r2b.apps.xuser.domain.usecase.filter.RemoveUsersFilterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher


@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    fun provideGetCurrentUserUseCase(
        userRepository: UserRepository,
        @BackgroundScope dispatcher: CoroutineDispatcher,
    ): GetCurrentUserUseCase =
        GetCurrentUserUseCase(
            userRepository,
            dispatcher,
        )

    @Provides
    fun provideGetUsersUseCase(
        userRepository: UserRepository,
        @BackgroundScope dispatcher: CoroutineDispatcher,
    ): GetUsersUseCase =
        GetUsersUseCase(
            userRepository,
            dispatcher,
        )

    @Provides
    fun provideSetCurrentUserUseCase(
        userRepository: UserRepository,
        @BackgroundScope dispatcher: CoroutineDispatcher,
    ): SetCurrentUserUseCase =
        SetCurrentUserUseCase(
            userRepository,
            dispatcher,
        )

    @Provides
    fun provideRemoveUserUseCase(
        userRepository: UserRepository,
        @BackgroundScope dispatcher: CoroutineDispatcher,
    ): RemoveUserUseCase =
        RemoveUserUseCase(
            userRepository,
            dispatcher,
        )

    @Provides
    fun provideFilterUsersUseCase(
        userRepository: UserRepository,
        @BackgroundScope dispatcher: CoroutineDispatcher,
    ): FilterUsersUseCase =
        FilterUsersUseCase(
            userRepository,
            dispatcher,
        )

    @Provides
    fun provideRemoveUsersFilterUseCase(
        userRepository: UserRepository,
        @BackgroundScope dispatcher: CoroutineDispatcher,
    ): RemoveUsersFilterUseCase =
        RemoveUsersFilterUseCase(
            userRepository,
            dispatcher,
        )

}
