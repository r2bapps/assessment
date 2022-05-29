package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.randomuser.entity.RandomUserErrorResponse
import com.r2b.apps.lib.api.randomuser.entity.RandomUserResponse
import com.r2b.apps.lib.api.randomuser.retrofit.RandomUserService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.test.LoadResourcesTestRule
import com.r2b.apps.test.parseJson
import com.r2b.apps.utils.Either
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
    val loadResourcesRule = LoadResourcesTestRule("random-user_200.json")

    @get:Rule
    val mockkRule = MockKRule(this)

    private val pageItems = 20
    private lateinit var dataSource: UserDataSourceImpl

    @MockK
    lateinit var service: RandomUserService

    @RelaxedMockK
    lateinit var logger: Logger

    private fun createDataSource() {
        dataSource = UserDataSourceImpl(service, logger, pageItems, retry = false)
    }

    @Test
    fun `list maps successful response and translates zero-based page to API page`() = runTest {
        val parsedBody: RandomUserResponse =
            requireNotNull(parseJson(loadResourcesRule.resource))
        coEvery { service.users(page = 3, results = pageItems) } returns
            NetworkResponse.Success(parsedBody)
        createDataSource()

        assertEquals(Either.Success(FAKE_USER_DTO_LIST), dataSource.list(page = 2))
        coVerify(exactly = 1) { service.users(page = 3, results = pageItems) }
    }

    @Test
    fun `list preserves HTTP error code and API message`() = runTest {
        coEvery { service.users(page = 1, results = pageItems) } returns
            NetworkResponse.ApiError(RandomUserErrorResponse(error = "Unavailable"), 503)
        createDataSource()

        assertEquals(
            Either.Failure(com.r2b.apps.xuser.data.api.entity.ErrorDTO(503, "Unavailable", "")),
            dataSource.list(0),
        )
    }

    @Test
    fun `list maps network errors as connectivity failures`() = runTest {
        coEvery { service.users(page = 1, results = pageItems) } returns
            NetworkResponse.NetworkError(IOException("offline"))
        createDataSource()

        assertEquals(
            Either.Failure(com.r2b.apps.xuser.data.api.entity.ErrorDTO(0, "No Internet connection", "offline")),
            dataSource.list(0),
        )
    }

    @Test
    fun `list maps unexpected errors without crashing`() = runTest {
        coEvery { service.users(page = 1, results = pageItems) } returns
            NetworkResponse.UnknownError(IllegalStateException("bad response"))
        createDataSource()

        assertEquals(
            Either.Failure(com.r2b.apps.xuser.data.api.entity.ErrorDTO(0, "Unknown error", "bad response")),
            dataSource.list(0),
        )
    }
}
