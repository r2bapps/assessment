package com.r2b.apps.xuser.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCurrentUserUseCaseTest {

    @get:Rule val mockkRule = MockKRule(this)

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
                assertEquals(expected, awaitItem())
                verify { runBlocking { repository.getCurrentUser() } }
                cancelAndConsumeRemainingEvents()
            }
        }

}