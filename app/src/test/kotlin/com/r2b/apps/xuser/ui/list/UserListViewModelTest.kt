package com.r2b.apps.xuser.ui.list

import app.cash.turbine.test
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.r2b.apps.test.CoroutineTestRule
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.FAKE_USER_LIST
import com.r2b.apps.xuser.domain.usecase.GetUsersUseCase
import com.r2b.apps.xuser.domain.usecase.RemoveUserUseCase
import com.r2b.apps.xuser.domain.usecase.filter.FilterUsersUseCase
import com.r2b.apps.xuser.ui.list.UserListAction.CloseSearch
import com.r2b.apps.xuser.ui.list.UserListAction.ConfirmRemoval
import com.r2b.apps.xuser.ui.list.UserListAction.RemovalRequested
import com.r2b.apps.xuser.ui.list.UserListAction.SearchQueryChanged
import com.r2b.apps.xuser.ui.list.UserListAction.ToggleSearch
import com.r2b.apps.xuser.ui.list.UserListAction.UndoRemoval
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var getUsersUseCase: GetUsersUseCase
    @MockK
    private lateinit var removeUserUseCase: RemoveUserUseCase
    @MockK
    private lateinit var filterUsersUseCase: FilterUsersUseCase

    private lateinit var viewModel: UserListViewModel

    @Before
    fun setUp() {
        viewModel = UserListViewModel(
            getUsersUseCase,
            removeUserUseCase,
            filterUsersUseCase,
            SavedStateHandle(),
        )
    }

    @Test
    fun `users stream switches between default filtered and default paging data`() = runTest {
        val defaultPage = PagingData.empty<User>()
        val filteredPage = PagingData.empty<User>()
        every { getUsersUseCase.execute() } returns flowOf(defaultPage)
        every { filterUsersUseCase.execute("Ricky") } returns flowOf(filteredPage)

        viewModel.uiState.value.pagingData.test {
            assertNotNull(awaitItem())
            viewModel.onAction(ToggleSearch)
            viewModel.onAction(SearchQueryChanged("Ricky"))
            assertNotNull(awaitItem())
            viewModel.onAction(CloseSearch)
            assertNotNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 2) { getUsersUseCase.execute() }
        verify(exactly = 1) { filterUsersUseCase.execute("Ricky") }
    }

    @Test
    fun `blank search uses the unfiltered user stream`() = runTest {
        every { getUsersUseCase.execute() } returns flowOf(PagingData.empty())

        viewModel.uiState.value.pagingData.test {
            awaitItem()
            viewModel.onAction(ToggleSearch)
            viewModel.onAction(SearchQueryChanged("   "))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { getUsersUseCase.execute() }
        verify(exactly = 0) { filterUsersUseCase.execute(any()) }
    }

    @Test
    fun `search state is restored from saved state`() = runTest {
        every { filterUsersUseCase.execute("Ricky") } returns flowOf(PagingData.empty())
        val restoredViewModel = UserListViewModel(
            getUsersUseCase,
            removeUserUseCase,
            filterUsersUseCase,
            SavedStateHandle(
                mapOf(
                    "search_expanded" to true,
                    "search_text" to "Ricky",
                ),
            ),
        )

        assertEquals(true, restoredViewModel.uiState.value.searchExpanded)
        assertEquals("Ricky", restoredViewModel.uiState.value.searchText)
        restoredViewModel.uiState.value.pagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { filterUsersUseCase.execute("Ricky") }
    }

    @Test
    fun `search and removal actions update the UI state`() = runTest {
        val user = FAKE_USER_LIST.first()
        val anotherUser = FAKE_USER_LIST[1]
        every { getUsersUseCase.execute() } returns flowOf(PagingData.empty())
        every { removeUserUseCase.execute(user.id) } returns flowOf(Unit)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.searchExpanded)

            viewModel.onAction(ToggleSearch)
            assertEquals(true, awaitItem().searchExpanded)

            viewModel.onAction(SearchQueryChanged("Ricky"))
            assertEquals("Ricky", awaitItem().searchText)

            viewModel.onAction(RemovalRequested(user))
            assertEquals(user, awaitItem().pendingRemoval)

            viewModel.onAction(RemovalRequested(anotherUser))
            assertEquals(user, viewModel.uiState.value.pendingRemoval)

            viewModel.onAction(UndoRemoval)
            assertNull(awaitItem().pendingRemoval)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirmed removal errors are exposed in the UI state`() = runTest {
        val error = IllegalStateException("delete failed")
        val user = User(
            id = 1,
            serverId = "server-1",
            name = "Ricky",
            surname = "",
            email = "",
            phone = "",
            picture = "",
            gender = "",
            location = "",
            registeredDate = "",
            description = "",
        )
        every { removeUserUseCase.execute(user.id) } returns flow { throw error }

        viewModel.uiState.test {
            awaitItem()
            viewModel.onAction(RemovalRequested(user))
            assertEquals(user, awaitItem().pendingRemoval)
            viewModel.onAction(ConfirmRemoval)
            assertNull(awaitItem().pendingRemoval)
            assertSame(error, awaitItem().error)
            viewModel.onAction(UserListAction.ErrorShown)
            assertNull(awaitItem().error)
            cancelAndIgnoreRemainingEvents()
        }
        advanceUntilIdle()
    }

    @Test
    fun `successful removal clears pending user without an error`() = runTest {
        val user = FAKE_USER_LIST.first()
        every { removeUserUseCase.execute(user.id) } returns flowOf(Unit)

        viewModel.uiState.test {
            awaitItem()
            viewModel.onAction(RemovalRequested(user))
            awaitItem()
            viewModel.onAction(ConfirmRemoval)
            awaitItem()
            advanceUntilIdle()
            assertNull(viewModel.uiState.value.error)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { removeUserUseCase.execute(user.id) }
    }
}
