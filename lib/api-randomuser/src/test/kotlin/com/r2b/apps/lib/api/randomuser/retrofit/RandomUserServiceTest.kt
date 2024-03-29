package com.r2b.apps.lib.api.randomuser.retrofit

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.getOrDefaultOrNull
import com.r2b.apps.lib.api.randomuser.entity.Results
import com.r2b.apps.test.MockWebServerTestWatcher
import com.r2b.apps.test.loadJson
import com.r2b.apps.test.retrofitBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection


class RandomUserServiceTest {

    // TODO: Should get apiService from DI! But requires to be android library module

    @get:Rule
    val mockWebServerRule = MockWebServerTestWatcher(MockWebServer())

    private val path = "/"
    private val seed = "seed"
    private val results = "results"
    private val page = "page"


    private lateinit var apiService: RandomUserService

    @Before
    fun setUp() {
        apiService = retrofitBuilder(getMockWebServer(), path)
            .build()
            .create(RandomUserService::class.java)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `When request pagination query arguments must be filled`() = runBlocking {
        val body: String = loadJson(this::class.java.classLoader, "random-user_200.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(body)
        )
        apiService.users()

        val recordedRequest: RecordedRequest = getMockWebServer().takeRequest()
        val url = recordedRequest.requestUrl

        assertEquals(RandomUserService.defaultSeed, url?.queryParameter(seed))
        assertEquals(RandomUserService.defaultResults.toString(), url?.queryParameter(results))
        assertEquals(RandomUserService.firstPage.toString(), url?.queryParameter(page))
    }

    @Test
    fun `When success a list of result is retrieved`() = runBlocking {
        val body: String = loadJson(this::class.java.classLoader, "random-user_200.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(body)
        )
        val response = apiService.users().getOrDefaultOrNull()

        assertEquals(20, response?.results?.size ?: 0)
        assertEquals(response?.info?.results, response?.results?.size)
        assert(response?.results?.all { it is Results } ?: false)
    }

    @Test
    fun `When server error ApiError is retrieved`() = runBlocking {
        val expectedCode = 500
        val expectedMessage = "Uh oh, something has gone wrong. Please tweet us @randomapi about the issue. Thank you."
        val body: String = loadJson(this::class.java.classLoader,"random-user_error.json")
        getMockWebServer().enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody(body)
        )
        val response = apiService.users()

        assert(response.isApiError())
        assertEquals(expectedMessage, (response as NetworkResponse.ApiError).body.error)
        assertEquals(expectedCode, (response as NetworkResponse.ApiError).code)
    }

    private fun getMockWebServer() = mockWebServerRule.mockWebServer

}