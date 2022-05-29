package com.r2b.apps.xuser.domain.usecase.user

import app.cash.turbine.test
import com.r2b.apps.xuser.domain.FAKE_USER_LIST
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
class GetUserUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var repository: UserRepository

    @Test
    fun `execute returns the user requested by id`() = runTest {
        val user = FAKE_USER_LIST[0]
        coEvery { repository.getUser(user.id) } returns user
        val useCase = GetUserUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute(user.id).test {
            assertEquals(user, awaitItem())
            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getUser(user.id) }
    }

    @Test
    fun `execute emits null when the user does not exist`() = runTest {
        val userId = 404
        coEvery { repository.getUser(userId) } returns null
        val useCase = GetUserUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute(userId).test {
            assertEquals(null, awaitItem())
            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getUser(userId) }
    }
}
