package com.r2b.apps.lib.api.marvel.retrofit

import com.r2b.apps.lib.api.marvel.MockWebServerTestWatcher
import com.r2b.apps.lib.api.marvel.entity.Result
import com.r2b.apps.lib.api.marvel.loadJson
import com.r2b.apps.lib.api.marvel.retrofitBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import java.net.HttpURLConnection


class MarvelServiceTest {

    // TODO: Should get apiService from DI! But requires to be android library module

    @get:Rule val mockWebServerRule = MockWebServerTestWatcher(MockWebServer())

    private val path = "/"
    private val limit = "limit"
    private val offset = "offset"

    private lateinit var apiService: MarvelService

    @Before
    fun setUp() {
        apiService = retrofitBuilder(getMockWebServer(), path)
            .build()
            .create(MarvelService::class.java)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `When request pagination query arguments must be filled`() = runBlocking {
        val body: String = loadJson(this::class.java.classLoader,"marvel_200.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(body)
        )
        apiService.characters()

        val recordedRequest: RecordedRequest = getMockWebServer().takeRequest()
        val url =  recordedRequest.requestUrl

        assertEquals(MarvelService.defaultLimit.toString(), url?.queryParameter(limit))
        assertEquals(MarvelService.defaultOffset.toString(), url?.queryParameter(offset))
    }

    @Test
    fun `When success a list of result is retrieved`() = runBlocking {
        val body: String = loadJson(this::class.java.classLoader,"marvel_200.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(body)
        )
        val response = apiService.characters()

        assertEquals(20, response.data?.results?.size ?: 0)
        assertEquals(response.data?.count?.toInt() ?: -1, response.data?.results?.size)
        assert(response.data?.results?.all { it is Result } ?: false)
    }

    @Test
    fun `When server error HttpException is thrown`() {
        val body: String = loadJson(this::class.java.classLoader,"marvel_409.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_CONFLICT)
                .setBody(body)
        )

        assertThrows(HttpException::class.java) { runBlocking { apiService.characters() } }
    }

    private fun getMockWebServer() = mockWebServerRule.mockWebServer

}