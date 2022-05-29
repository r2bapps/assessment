package com.r2b.apps.xuser.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemoveUsersFilterUseCaseTest {

    @get:Rule val mockkRule = MockKRule(this)

    private lateinit var useCase: RemoveUsersFilterUseCase

    @MockK
    lateinit var repository: UserRepository

    @Before
    fun setUp() {
        useCase = RemoveUsersFilterUseCase(repository, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun execute() {
        every { runBlocking { repository.removeFilter() } } returns FAKE_USER_LIST
        val expected = FAKE_USER_LIST

        runBlocking {
            useCase.execute().test {
                assertEquals(expected, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

}