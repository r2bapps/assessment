package com.r2b.apps.xuser.domain.usecase

import app.cash.turbine.test
import androidx.paging.PagingData
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetUsersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var repository: UserRepository

    @Test
    fun `execute emits the repository paging data`() = runTest {
        val pagingData = PagingData.empty<User>()
        every { repository.list() } returns flowOf(pagingData)
        val useCase = GetUsersUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute().test {
            assertSame(pagingData, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.list() }
    }

    @Test
    fun `execute propagates repository failures`() = runTest {
        val failure = IllegalStateException("database unavailable")
        every { repository.list() } returns flow { throw failure }
        val useCase = GetUsersUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute().test {
            val error = awaitError()
            assertEquals(failure::class, error::class)
            assertEquals(failure.message, error.message)
        }
    }
}
