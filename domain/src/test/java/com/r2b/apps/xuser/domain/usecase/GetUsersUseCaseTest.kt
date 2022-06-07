package com.r2b.apps.xuser.domain.usecase

import app.cash.turbine.test
import com.r2b.apps.xuser.domain.FAKE_USER_LIST
import com.r2b.apps.xuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.*

class GetUsersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var useCase: GetUsersUseCase

    @MockK
    lateinit var repository: UserRepository

    @Before
    fun setup() {
        useCase = GetUsersUseCase(repository, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun execute() {
        every { runBlocking { repository.list(any()) } } returns FAKE_USER_LIST
        val page = 0
        val expected = FAKE_USER_LIST

        runBlocking {
            useCase.execute(page).test {
                Assert.assertEquals(expected, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

}