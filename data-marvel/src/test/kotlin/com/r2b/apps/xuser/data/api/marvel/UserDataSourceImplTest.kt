package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.marvel.entity.MarvelCharacterResponse
import com.r2b.apps.lib.api.marvel.retrofit.MarvelService
import com.r2b.apps.lib.api.successNetworkResponse
import com.r2b.apps.lib.logger.Logger
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserDataSourceImplTest {

    @get:Rule val loadResourcesRule = LoadResourcesTestRule("marvel_200.json")
    @get:Rule val mockkRule = MockKRule(this)

    private val id = "id"
    private val pageItems = 20
    private val wsRetry = false
    private lateinit var dataSource: UserDataSourceImpl

    @MockK lateinit var service: MarvelService
    @RelaxedMockK lateinit var logger: Logger

    @Before
    fun setUp() {
        dataSource = UserDataSourceImpl(service, logger, pageItems, wsRetry)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun list() =
        runBlocking {
            val page = 0
            val parsedBody: MarvelCharacterResponse = parseJson(loadResourcesRule.resource) ?: MarvelCharacterResponse()
            every { runBlocking { service.characters() } } returns successNetworkResponse(parsedBody)
            val expected = FAKE_USER_DTO_LIST

            val response = dataSource.list(page)

            assertEquals(expected, response)
        }

}
