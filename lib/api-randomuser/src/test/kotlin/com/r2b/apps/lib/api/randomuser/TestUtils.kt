package com.r2b.apps.lib.api.randomuser

import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.nio.charset.Charset

// TODO: Shared multi module test utils

fun retrofitBuilder(mockWebServer: MockWebServer, path: String): Retrofit.Builder =
    Retrofit
        .Builder()
        .baseUrl(mockWebServer.url(path))
        .addConverterFactory(MoshiConverterFactory.create())

fun loadJson(classLoader: ClassLoader, name: String): String =
    classLoader.getResource(name)?.readText(charset = Charset.forName("UTF8")) ?: ""

