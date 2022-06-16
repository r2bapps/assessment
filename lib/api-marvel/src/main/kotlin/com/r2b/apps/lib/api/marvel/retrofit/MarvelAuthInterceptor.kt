package com.r2b.apps.lib.api.marvel.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.math.BigInteger
import java.security.MessageDigest
// TODO: invalid auth response interceptor for changing 401 with unknown body to the same body than 409
class MarvelAuthInterceptor (
    private val publicKey: String,
    private val privateKey: String,
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val timestamp = System.currentTimeMillis().toString()

        val original = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("apikey", publicKey)
            .addQueryParameter("ts", timestamp)
            .addQueryParameter("hash", hash(timestamp))
            .build()
        val request = original.newBuilder().url(url).build()

        return chain.proceed(request)
    }

    private fun hash(timestamp: String): String =
        md5(timestamp + privateKey + publicKey)

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

}
