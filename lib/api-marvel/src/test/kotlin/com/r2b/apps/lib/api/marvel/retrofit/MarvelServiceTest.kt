package com.r2b.apps.lib.api.marvel.retrofit

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.test.MockWebServerTestWatcher
import com.r2b.apps.test.loadJson
import com.r2b.apps.lib.api.getOrDefaultOrNull
import com.r2b.apps.lib.api.marvel.entity.MarvelErrorResponse
import com.r2b.apps.lib.api.marvel.entity.Result
import com.r2b.apps.test.retrofitBuilder
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
        val response = apiService.characters().getOrDefaultOrNull()

        assertEquals(20, response?.data?.results?.size ?: 0)
        assertEquals(response?.data?.count?.toInt() ?: -1, response?.data?.results?.size)
        assert(response?.data?.results?.all { it is Result } ?: false)
    }

    @Test
    fun `When server error ApiError is retrieved`() = runBlocking {
        val expectedCode = 409
        val expectedError = MarvelErrorResponse(code = 409, status = "You may not request more than 100 items.")
        val body: String = loadJson(this::class.java.classLoader,"marvel_409.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_CONFLICT)
                .setBody(body)
        )
        val response = apiService.characters()

        assert(response.isApiError())
        assertEquals(expectedError, (response as NetworkResponse.ApiError).body)
        assertEquals(expectedCode, (response as NetworkResponse.ApiError).code)

    }

    @Test
    fun `When server error UnknownError is retrieved`() = runBlocking {
        val expectedMessage = "Response is fail but the error body is null"
        val body: String = loadJson(this::class.java.classLoader,"marvel_401.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .setBody(body)
        )
        val response = apiService.characters()

        assert(response.isUnknownError())
        assertEquals(expectedMessage, (response as NetworkResponse.UnknownError).error.message)
    }

    private fun getMockWebServer() = mockWebServerRule.mockWebServer

}