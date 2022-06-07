package com.r2b.apps.xuser.domain.usecase.filter

import app.cash.turbine.test
import com.r2b.apps.xuser.domain.FAKE_USER_LIST
import com.r2b.apps.xuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.*

class FilterUsersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var useCase: FilterUsersUseCase

    @MockK
    lateinit var repository: UserRepository

    @Before
    fun setup() {
        useCase = FilterUsersUseCase(repository, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun execute() {
        every { runBlocking { repository.filterUsersBy(any()) } } returns FAKE_USER_LIST
        val text = "text"
        val expected = FAKE_USER_LIST

        runBlocking {
            useCase.execute(text).test {
                Assert.assertEquals(expected, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

}