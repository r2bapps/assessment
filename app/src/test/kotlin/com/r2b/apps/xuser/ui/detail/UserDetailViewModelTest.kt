package com.r2b.apps.xuser.ui.detail

import app.cash.turbine.test
import com.r2b.apps.test.CoroutineTestRule
import com.r2b.apps.xuser.domain.usecase.user.GetUserUseCase
import com.r2b.apps.xuser.ui.FAKE_USER_LIST
import com.r2b.apps.xuser.ui.detail.UserDetailAction.Load
import com.r2b.apps.xuser.ui.detail.UserDetailAction.Retry
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.UserUiState.Content
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.UserUiState.Error
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.UserUiState.Loading
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.UserUiState.NotFound
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var getUserUseCase: GetUserUseCase

    private lateinit var viewModel: UserDetailViewModel

    @Before
    fun setUp() {
        viewModel = UserDetailViewModel(getUserUseCase)
    }

    @Test
    fun `load emits content for the requested user`() = runTest {
        val user = FAKE_USER_LIST[0]
        every { getUserUseCase.execute(user.id) } returns flow { emit(user) }

        viewModel.uiState.test {
            assertEquals(Loading, awaitItem())
            viewModel.onAction(Load(user.id))
            assertEquals(Content(user), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load emits not found when the user does not exist`() = runTest {
        every { getUserUseCase.execute(any()) } returns flow { emit(null) }

        viewModel.uiState.test {
            assertEquals(Loading, awaitItem())
            viewModel.onAction(Load(123))
            assertEquals(NotFound, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load exposes errors as UI state`() = runTest {
        val error = Exception("error")
        every { getUserUseCase.execute(any()) } returns flow { throw error }

        viewModel.uiState.test {
            assertEquals(Loading, awaitItem())
            viewModel.onAction(Load(123))
            assertEquals(Error(error), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load can be retried after an error and emits content`() = runTest {
        val user = FAKE_USER_LIST.first()
        val failure = IllegalStateException("temporary failure")
        every { getUserUseCase.execute(user.id) } returnsMany listOf(
            flow { throw failure },
            flowOf(user),
        )

        viewModel.uiState.test {
            assertEquals(Loading, awaitItem())
            viewModel.onAction(Load(user.id))
            assertEquals(Error(failure), awaitItem())
            viewModel.onAction(Retry)
            assertEquals(Loading, awaitItem())
            assertEquals(Content(user), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
