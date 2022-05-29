package com.r2b.apps.xuser.ui

import com.r2b.apps.xuser.domain.GetCurrentUserUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserDetailViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: UserDetailViewModel

    @MockK private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
        viewModel = UserDetailViewModel(
            getCurrentUserUseCase,
        )
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runBlocking {

        every { getCurrentUserUseCase.execute() } returns flow { emit(FAKE_USER_LIST[0]) }
        val expectedSuccess = UserDetailViewModel.UserUiState.Success(FAKE_USER_LIST[0])

        viewModel.load()

        assertEquals(UserDetailViewModel.UserUiState.Idle, viewModel.uiState.first())
        assertEquals(expectedSuccess, viewModel.uiState.drop(1).first())
        assertEquals(UserDetailViewModel.UserUiState.Idle, viewModel.uiState.drop(2).first())
    }

}
