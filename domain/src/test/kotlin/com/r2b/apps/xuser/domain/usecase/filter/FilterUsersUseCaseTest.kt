package com.r2b.apps.xuser.domain.usecase.filter

import app.cash.turbine.test
import androidx.paging.PagingData
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterUsersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var repository: UserRepository

    @Test
    fun `execute passes the exact query and emits repository paging data`() = runTest {
        val query = "  Ricky  "
        val pagingData = PagingData.empty<User>()
        every { repository.filterUsersBy(query) } returns flowOf(pagingData)
        val useCase = FilterUsersUseCase(repository, UnconfinedTestDispatcher(testScheduler))

        useCase.execute(query).test {
            assertSame(pagingData, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.filterUsersBy(query) }
    }
}
