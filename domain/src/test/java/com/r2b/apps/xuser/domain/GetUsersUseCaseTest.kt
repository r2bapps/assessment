package com.r2b.apps.xuser.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetUsersUseCaseTest {

    @get:Rule val mockkRule = MockKRule(this)

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
                assertEquals(expected, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

}
