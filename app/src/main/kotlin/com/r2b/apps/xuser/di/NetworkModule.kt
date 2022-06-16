package com.r2b.apps.xuser.di

import com.r2b.apps.lib.api.NetworkResponseAdapterFactory
import com.r2b.apps.xuser.di.qualifier.ConnectTimeoutInSeconds
import com.r2b.apps.xuser.di.qualifier.ReadTimeoutInSeconds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    // TODO: Extract to config file
    @ReadTimeoutInSeconds
    @Provides
    fun provideReadTimeoutInSeconds(): Int = 15

    // TODO: Extract to config file
    @ConnectTimeoutInSeconds
    @Provides
    fun provideConnectTimeoutInSeconds(): Int = 15

    @Provides
    fun provideHttpLoggingInterceptorLevel(): HttpLoggingInterceptor.Level =
        HttpLoggingInterceptor.Level.BODY

    @Singleton
    @Provides
    fun provideConverterFactory(): retrofit2.Converter.Factory =
        MoshiConverterFactory.create()

    @Singleton
    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory =
        NetworkResponseAdapterFactory()

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(
        level: HttpLoggingInterceptor.Level,
    ): Interceptor =
        HttpLoggingInterceptor().apply {setLevel( level )}

}