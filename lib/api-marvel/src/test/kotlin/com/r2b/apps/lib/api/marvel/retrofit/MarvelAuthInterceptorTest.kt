package com.r2b.apps.lib.api.marvel.retrofit

import com.r2b.apps.test.MockWebServerTestWatcher
import com.r2b.apps.test.loadJson
import com.r2b.apps.lib.api.marvel.md5
import com.r2b.apps.test.retrofitBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection

class MarvelAuthInterceptorTest {

    // TODO: Get dependencies from DI? Requires to be android library module

    @get:Rule val mockWebServerRule = MockWebServerTestWatcher(MockWebServer())

    private val path = "/"
    private val publicKey = "publicKey"
    private val privateKey = "privateKey"
    private val apikey = "apikey"
    private val ts = "ts"
    private val hash = "hash"

    private lateinit var apiService: MarvelService
    private lateinit var body: String

    @Before
    fun setUp() {
        apiService = retrofitBuilder(getMockWebServer(), path)
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(
                        MarvelAuthInterceptor(
                            publicKey,
                            privateKey,
                        )
                    )
                    .build()
            )
            .build()
            .create(MarvelService::class.java)
        body = loadJson(this::class.java.classLoader,"marvel_200.json")
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `When request authorization query arguments must be filled`() = runBlocking {
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(body)
        )
        apiService.characters()

        val recordedRequest: RecordedRequest = getMockWebServer().takeRequest()
        val url =  recordedRequest.requestUrl
        val timestamp = url?.queryParameter("ts")
        val queryParams = url?.queryParameterNames ?: emptySet()

        assertEquals(publicKey, url?.queryParameter(apikey))
        assert(queryParams.contains(ts))
        assertEquals(md5(timestamp + privateKey + publicKey), url?.queryParameter(hash))

    }

    private fun getMockWebServer() = mockWebServerRule.mockWebServer

}