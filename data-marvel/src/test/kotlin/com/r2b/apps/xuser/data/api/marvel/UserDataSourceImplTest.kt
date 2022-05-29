package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiError
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiErrorResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabasePage
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseQuery
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseResponse
import com.r2b.apps.lib.api.marvel.retrofit.MarvelDatabaseService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.utils.Either
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UserDataSourceImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val pageItems = 20
    private lateinit var dataSource: UserDataSourceImpl

    @MockK
    lateinit var service: MarvelDatabaseService

    @RelaxedMockK
    lateinit var logger: Logger

    private fun createDataSource() {
        dataSource = UserDataSourceImpl(service, logger, pageItems, retry = false)
    }

    @Test
    fun `list maps successful response and requests the corresponding zero-based offset`() = runTest {
        val body = MarvelDatabaseResponse(
            query = MarvelDatabaseQuery(
                pages = listOf(MarvelDatabasePage(pageId = 42, index = 1, title = "Spider-Man")),
            ),
        )
        coEvery { service.characters(limit = pageItems, offset = 40) } returns
            NetworkResponse.Success(body)
        createDataSource()

        assertEquals(
            Either.Success(
                listOf(
                    UserDTO(
                        serverId = "42",
                        name = "Spider-Man",
                        picture = "",
                        description = "",
                    ),
                ),
            ),
            dataSource.list(page = 2),
        )
        coVerify(exactly = 1) { service.characters(limit = pageItems, offset = 40) }
    }

    @Test
    fun `list maps API errors with the HTTP status code`() = runTest {
        coEvery { service.characters(limit = pageItems, offset = 0) } returns
            NetworkResponse.ApiError(
                MarvelDatabaseApiErrorResponse(
                    MarvelDatabaseApiError("badvalue", "Invalid query"),
                ),
                400,
            )
        createDataSource()

        assertEquals(
            Either.Failure(ErrorDTO(400, "Invalid query", "badvalue")),
            dataSource.list(0),
        )
    }

    @Test
    fun `list maps API errors returned with a successful HTTP response`() = runTest {
        coEvery { service.characters(limit = pageItems, offset = 0) } returns
            NetworkResponse.Success(
                MarvelDatabaseResponse(error = MarvelDatabaseApiError("readapidenied", "Access denied")),
            )
        createDataSource()

        assertEquals(
            Either.Failure(ErrorDTO(0, "Access denied", "readapidenied")),
            dataSource.list(0),
        )
    }

    @Test
    fun `list maps network errors as connectivity failures`() = runTest {
        coEvery { service.characters(limit = pageItems, offset = 0) } returns
            NetworkResponse.NetworkError(IOException("offline"))
        createDataSource()

        assertEquals(
            Either.Failure(ErrorDTO(0, "No Internet connection", "offline")),
            dataSource.list(0),
        )
    }

    @Test
    fun `list maps unexpected errors without crashing`() = runTest {
        coEvery { service.characters(limit = pageItems, offset = 0) } returns
            NetworkResponse.UnknownError(IllegalStateException("bad response"))
        createDataSource()

        assertEquals(
            Either.Failure(ErrorDTO(0, "Unknown error", "bad response")),
            dataSource.list(0),
        )
    }
}
