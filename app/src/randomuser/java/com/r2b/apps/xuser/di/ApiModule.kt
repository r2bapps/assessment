package com.r2b.apps.xuser.di

import com.r2b.apps.lib.api.randomuser.retrofit.RandomUserService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.randomuser.UserDataSourceImpl
import com.r2b.apps.xuser.di.qualifier.ConnectTimeoutInSeconds
import com.r2b.apps.xuser.di.qualifier.Pagination
import com.r2b.apps.xuser.di.qualifier.ReadTimeoutInSeconds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: Interceptor,
        @ReadTimeoutInSeconds readTimeoutInSeconds: Int,
        @ConnectTimeoutInSeconds connectTimeoutInSeconds: Int,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .readTimeout(readTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
            .connectTimeout(connectTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRandomUserService(
        client: OkHttpClient,
        converterFactory: retrofit2.Converter.Factory,
    ) : RandomUserService =
        Retrofit
            .Builder()
            .baseUrl(RandomUserService.baseUrl)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(RandomUserService::class.java)

    @Singleton
    @Provides
    fun provideUserDataSource(
        randomUserService: RandomUserService,
        logger: Logger,
        @Pagination pageItems: Int
    ): UserDataSource =
        UserDataSourceImpl(
            randomUserService,
            logger,
            pageItems,
        )

}