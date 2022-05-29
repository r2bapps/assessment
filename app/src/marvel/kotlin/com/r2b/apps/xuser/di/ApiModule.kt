package com.r2b.apps.xuser.di

import com.r2b.apps.lib.api.marvel.retrofit.MarvelDatabaseService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.marvel.UserDataSourceImpl
import com.r2b.apps.xuser.di.qualifier.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
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
            .addInterceptor { chain ->
                val request = chain.request()
                val updatedRequest = if (request.url.host == "static.wikia.nocookie.net") {
                    request.newBuilder()
                        .header("Referer", MarvelDatabaseService.baseUrl)
                        .build()
                } else {
                    request
                }
                chain.proceed(updatedRequest)
            }
            .build()

    @Singleton
    @Provides
    fun provideMarvelDatabaseService(
        client: OkHttpClient,
        converterFactory: retrofit2.Converter.Factory,
        callAdapter: CallAdapter.Factory,
    ): MarvelDatabaseService =
        Retrofit
            .Builder()
            .baseUrl(MarvelDatabaseService.baseUrl)
            .client(client)
            .addCallAdapterFactory(callAdapter)
            .addConverterFactory(converterFactory)
            .build()
            .create(MarvelDatabaseService::class.java)

    @Singleton
    @Provides
    fun provideUserDataSource(
        marvelDatabaseService: MarvelDatabaseService,
        logger: Logger,
        @Pagination pageItems: Int,
        @WSRetry wsRetry: Boolean,
    ): UserDataSource =
        UserDataSourceImpl(
            marvelDatabaseService,
            logger,
            pageItems,
            wsRetry,
        )

}