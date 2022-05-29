package com.r2b.apps.xuser.domain.usecase

import app.cash.turbine.test
import com.r2b.apps.xuser.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemoveUserUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var repository: UserRepository

    @Test
    fun `execute removes the requested id and emits completion`() = runTest {
        val userId = 42
        coEvery { repository.removeUser(userId) } returns Unit
        val useCase = RemoveUserUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute(userId).test {
            awaitItem()
            awaitComplete()
        }

        coVerify(exactly = 1) { repository.removeUser(userId) }
    }

    @Test
    fun `execute propagates repository failures`() = runTest {
        val failure = IllegalStateException("remove failed")
        coEvery { repository.removeUser(any()) } throws failure
        val useCase = RemoveUserUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute(42).test {
            val error = awaitError()
            assertEquals(failure::class, error::class)
            assertEquals(failure.message, error.message)
        }
    }
}
