package com.r2b.apps.test

import com.r2b.apps.lib.api.NetworkResponseAdapterFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.nio.charset.Charset

fun retrofitBuilder(mockWebServer: MockWebServer, path: String): Retrofit.Builder =
    Retrofit
        .Builder()
        .baseUrl(mockWebServer.url(path))
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create())

fun loadJson(classLoader: ClassLoader, name: String): String =
    classLoader.getResource(name)?.readText(charset = Charset.forName("UTF8")) ?: ""

inline fun <reified T> parseJson(jsonString: String): T? {
    val moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
    return jsonAdapter.fromJson(jsonString)
}
