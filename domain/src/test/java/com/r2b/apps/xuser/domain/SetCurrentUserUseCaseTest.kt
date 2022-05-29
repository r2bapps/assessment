package com.r2b.apps.xuser.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetCurrentUserUseCaseTest {

    @get:Rule val mockkRule = MockKRule(this)

    private val id = 0
    private lateinit var useCase: SetCurrentUserUseCase

    @MockK
    lateinit var repository: UserRepository

    @Before
    fun setUp() {
        useCase = SetCurrentUserUseCase(repository, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun execute() =
        runBlocking {
            useCase.execute(id).test {
                verify { runBlocking { repository.setCurrentUser(id) } }
                cancelAndConsumeRemainingEvents()
            }
        }

}