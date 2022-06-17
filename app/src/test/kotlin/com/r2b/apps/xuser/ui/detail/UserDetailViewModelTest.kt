package com.r2b.apps.xuser.ui.detail

import app.cash.turbine.test
import com.r2b.apps.test.CoroutineTestRule
import com.r2b.apps.xuser.domain.usecase.currentuser.GetCurrentUserUseCase
import com.r2b.apps.xuser.ui.FAKE_USER_LIST
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.Event.Error
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.UserUiState.Idle
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel.UserUiState.Success
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    private lateinit var viewModel: UserDetailViewModel

    @MockK
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Before
    fun setUp() {
        viewModel = UserDetailViewModel(
            getCurrentUserUseCase,
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun load() = runTest {
        // Given:
        every { getCurrentUserUseCase.execute() } returns flow { emit(FAKE_USER_LIST[0]) }
        val expected = Success(FAKE_USER_LIST[0])

        // When:
        viewModel.load()

        // Then:
        viewModel.uiState.test {
            assertEquals(Idle, awaitItem())
            assertEquals(expected, awaitItem())
            assertEquals(Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `when load throws an error this one is captured`() = runTest {
        // Given:
        val error = Exception("error")
        every { getCurrentUserUseCase.execute() } returns flow { throw error }
        val expected = Error(error)

        // When:
        viewModel.load()

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}