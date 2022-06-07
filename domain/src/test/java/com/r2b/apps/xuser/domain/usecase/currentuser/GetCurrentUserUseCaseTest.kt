package com.r2b.apps.xuser.domain.usecase.currentuser

import app.cash.turbine.test
import com.r2b.apps.xuser.domain.FAKE_USER_LIST
import com.r2b.apps.xuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.*

class GetCurrentUserUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var useCase: GetCurrentUserUseCase

    @MockK
    lateinit var repository: UserRepository

    @Before
    fun setUp() {
        useCase = GetCurrentUserUseCase(repository, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun execute() =
        runBlocking {
            every { runBlocking { repository.getCurrentUser() } } returns FAKE_USER_LIST[0]
            val expected = FAKE_USER_LIST[0]

            useCase.execute().test {
                Assert.assertEquals(expected, awaitItem())
                verify { runBlocking { repository.getCurrentUser() } }
                cancelAndConsumeRemainingEvents()
            }
        }

}