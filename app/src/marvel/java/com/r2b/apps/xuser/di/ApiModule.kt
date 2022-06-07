package com.r2b.apps.xuser.di

import com.r2b.apps.lib.api.marvel.retrofit.MarvelAuthInterceptor
import com.r2b.apps.lib.api.marvel.retrofit.MarvelService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.BuildConfig
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.marvel.UserDataSourceImpl
import com.r2b.apps.xuser.di.qualifier.ConnectTimeoutInSeconds
import com.r2b.apps.xuser.di.qualifier.Pagination
import com.r2b.apps.xuser.di.qualifier.MarvelAuth
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

    private val publicKey: String = BuildConfig.MARVEL_PUBLIC_KEY
    private val privateKey: String = BuildConfig.MARVEL_PRIVATE_KEY

    @MarvelAuth
    @Singleton
    @Provides
    fun provideMarvelAuthInterceptor(): Interceptor =
        MarvelAuthInterceptor(
            publicKey,
            privateKey,
        )

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: Interceptor,
        @MarvelAuth marvelAuthInterceptor: Interceptor,
        @ReadTimeoutInSeconds readTimeoutInSeconds: Int,
        @ConnectTimeoutInSeconds connectTimeoutInSeconds: Int,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .readTimeout(readTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
            .connectTimeout(connectTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(marvelAuthInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideMarvelService(
        client: OkHttpClient,
        converterFactory: retrofit2.Converter.Factory,
    ) : MarvelService =
        Retrofit
            .Builder()
            .baseUrl(MarvelService.baseUrl)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(MarvelService::class.java)

    @Singleton
    @Provides
    fun provideUserDataSource(
        marvelUserService: MarvelService,
        logger: Logger,
        @Pagination pageItems: Int
    ): UserDataSource =
        UserDataSourceImpl(
            marvelUserService,
            logger,
            pageItems,
        )

}