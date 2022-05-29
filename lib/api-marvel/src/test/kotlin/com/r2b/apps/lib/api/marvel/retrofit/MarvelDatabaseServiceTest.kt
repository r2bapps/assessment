package com.r2b.apps.lib.api.marvel.retrofit

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.test.MockWebServerTestWatcher
import com.r2b.apps.test.loadJson
import com.r2b.apps.test.retrofitBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection

class MarvelDatabaseServiceTest {

    @get:Rule
    val mockWebServerRule = MockWebServerTestWatcher(MockWebServer())

    private lateinit var apiService: MarvelDatabaseService

    @Before
    fun setUp() {
        apiService = retrofitBuilder(mockWebServerRule.mockWebServer, "/")
            .build()
            .create(MarvelDatabaseService::class.java)
    }

    @Test
    fun `request uses the Marvel Database character category and requested offset`() = runBlocking {
        mockWebServerRule.mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""{"query":{"pages":[]}}"""),
        )

        apiService.characters(limit = 20, offset = 40)

        val request = mockWebServerRule.mockWebServer.takeRequest()
        val url = request.requestUrl
        assertEquals("/api.php", url?.encodedPath)
        assertEquals("query", url?.queryParameter("action"))
        assertEquals("search", url?.queryParameter("generator"))
        assertEquals("incategory:Characters", url?.queryParameter("gsrsearch"))
        assertEquals("0", url?.queryParameter("gsrnamespace"))
        assertEquals("20", url?.queryParameter("gsrlimit"))
        assertEquals("40", url?.queryParameter("gsroffset"))
        assertEquals("pageimages|info", url?.queryParameter("prop"))
        assertEquals("2", url?.queryParameter("formatversion"))
        assertEquals("XUserAndroid/1.0 (Marvel Database client)", request.getHeader("User-Agent"))
    }

    @Test
    fun `successful response maps category search pages`() = runBlocking {
        mockWebServerRule.mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(loadJson(this::class.java.classLoader, "marvel_database_characters.json")),
        )

        val response = apiService.characters()

        assertTrue(response is NetworkResponse.Success)
        val page = (response as NetworkResponse.Success).body.query?.pages?.single()
        assertEquals(8190L, page?.pageId)
        assertEquals("Spider-Man", page?.title)
        assertEquals(
            "https://static.wikia.nocookie.net/marveldatabase/images/spider-man.jpg",
            page?.thumbnail?.source,
        )
    }

    @Test
    fun `HTTP errors are exposed as API errors`() = runBlocking {
        mockWebServerRule.mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
                .setBody(loadJson(this::class.java.classLoader, "marvel_database_error.json")),
        )

        val response = apiService.characters()

        assertTrue(response is NetworkResponse.ApiError)
        assertEquals(
            "readapidenied",
            (response as NetworkResponse.ApiError).body.error?.code,
        )
        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, response.code)
    }

    @Test
    fun `server disconnect is represented as a network error`() = runBlocking {
        mockWebServerRule.mockWebServer.enqueue(
            MockResponse().setSocketPolicy(
                okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START,
            ),
        )

        assertTrue(apiService.characters().isNetworkError())
    }
}
