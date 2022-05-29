package com.r2b.apps.lib.api.marvel

import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest

// TODO: Shared multi module test utils

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun retrofitBuilder(mockWebServer: MockWebServer, path: String): Retrofit.Builder =
    Retrofit
        .Builder()
        .baseUrl(mockWebServer.url(path))
        .addConverterFactory(MoshiConverterFactory.create())

fun loadJson(classLoader: ClassLoader, name: String): String =
    classLoader.getResource(name)?.readText(charset = Charset.forName("UTF8")) ?: ""

