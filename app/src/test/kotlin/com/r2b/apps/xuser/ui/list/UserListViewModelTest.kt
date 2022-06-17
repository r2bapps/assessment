package com.r2b.apps.xuser.ui.list

import app.cash.turbine.test
import com.r2b.apps.test.CoroutineTestRule
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.domain.usecase.GetUsersUseCase
import com.r2b.apps.xuser.domain.usecase.RemoveUserUseCase
import com.r2b.apps.xuser.domain.usecase.currentuser.SetCurrentUserUseCase
import com.r2b.apps.xuser.domain.usecase.filter.FilterUsersUseCase
import com.r2b.apps.xuser.domain.usecase.filter.RemoveUsersFilterUseCase
import com.r2b.apps.xuser.ui.FAKE_USER_LIST
import com.r2b.apps.xuser.ui.FAKE_USER_LIST_ITEM
import com.r2b.apps.xuser.ui.FakesBuilder
import com.r2b.apps.xuser.ui.list.UserListViewModel.Event.*
import com.r2b.apps.xuser.ui.list.UserListViewModel.UserUiState.*
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

    private lateinit var viewModel: UserListViewModel

    @MockK
    private lateinit var getUsersUseCase: GetUsersUseCase
    @MockK
    private lateinit var setUserUseCase: SetCurrentUserUseCase
    @MockK
    private lateinit var removeUserUseCase: RemoveUserUseCase
    @MockK
    private lateinit var filterUsersUseCase: FilterUsersUseCase
    @MockK
    private lateinit var removeUsersFilterUseCase: RemoveUsersFilterUseCase

    @Before
    fun setUp() {
        viewModel = UserListViewModel(
            getUsersUseCase,
            setUserUseCase,
            removeUserUseCase,
            filterUsersUseCase,
            removeUsersFilterUseCase,
        )
        viewModel.isLastPage = false
        viewModel.isLoadingMoreEnabled = true
    }

    @After
    fun tearDown() {
    }

    @Test
    fun load() = runTest {
        // Given:
        every { getUsersUseCase.execute(any()) } returns flow { emit(successEither(FAKE_USER_LIST)) }
        val expected = Load(FAKE_USER_LIST_ITEM)

        // When:
        viewModel.load()

        // Then:
        viewModel.event.test {
            viewModel.uiState.test {
                assertEquals(Idle, awaitItem())
                assertEquals(expected, awaitItem())
                assertEquals(Idle, awaitItem())
            }
            assertEquals(Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load when is last page does nothing`() = runTest {
        // Given:
        every { getUsersUseCase.execute(any()) } returns flow { emit(successEither(FAKE_USER_LIST)) }

        // When:
        viewModel.load()

        // Then:
        viewModel.uiState.test {
            assertEquals(Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when load throws an error this one is captured`() = runTest {
        // Given:
        viewModel.isLastPage = false
        val error = Exception("error")
        every { getUsersUseCase.execute(any()) } returns flow { throw error }
        val expected = GlobalError(error)

        // When:
        viewModel.load()

        // Then:
        viewModel.event.test {
            assertEquals(Loading, awaitItem())
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `nextPage when loading more is enabled and is not last page`() = runTest {
        // Given:
        every { getUsersUseCase.execute(any()) } returns flow { emit(successEither(FAKE_USER_LIST)) }
        val expected = LoadMore(FAKE_USER_LIST_ITEM)

        // When:
        viewModel.nextPage()

        // Then:
        viewModel.event.test {
            assertEquals(LoadingMore, awaitItem())
            viewModel.uiState.test {
                assertEquals(expected, awaitItem())
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `nextPage when is last page does nothing`() = runTest {
        // Given:
        viewModel.isLastPage = true
        every { getUsersUseCase.execute(any()) } returns flow { emit(successEither(emptyList())) }

        // When:
        viewModel.nextPage()

        // Then:
        viewModel.uiState.test {
            assertEquals(Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when nextPage throws an error this one is captured`() = runTest {
        // Given:
        val error = Exception("error")
        every { getUsersUseCase.execute(any()) } returns flow { throw error }
        val expected = GlobalError(error)

        // When:
        viewModel.nextPage()

        // Then:
        viewModel.event.test {
            assertEquals(LoadingMore, awaitItem())
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun removeUser() = runTest {
        // Given:
        val position = 12
        val user = FakesBuilder().user(id = 123)
        every { removeUserUseCase.execute(any()) } returns flow { emit( Unit ) }
        val expected = UserDeleted(position)

        // When:
        viewModel.removeUser(position, user)

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when removeUser throws an error this one is captured`() = runTest {
        // Given:
        val position = 12
        val user = FakesBuilder().user(id = 123)
        val error = Exception("error")
        every { removeUserUseCase.execute(any()) } returns flow { throw error }
        val expected = GlobalError(error)

        // When:
        viewModel.removeUser(position, user)

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectUser() = runTest {
        // Given:
        val user = FakesBuilder().user(id = 123)
        every { setUserUseCase.execute(any()) } returns flow { emit( Unit ) }
        val expected = UserSelected

        // When:
        viewModel.selectUser(user.id)

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when selectUser throws an error this one is captured`() = runTest {
        // Given:
        val user = FakesBuilder().user(id = 123)
        val error = Exception("error")
        every { setUserUseCase.execute(any()) } returns flow { throw error }
        val expected = GlobalError(error)

        // When:
        viewModel.selectUser(user.id)

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun filter() = runTest {
        // Given:
        val text = "123"
        every { filterUsersUseCase.execute(any()) } returns flow { emit(FAKE_USER_LIST) }
        val expected = Filter(FAKE_USER_LIST_ITEM)

        // When:
        viewModel.filter(text)

        // Then:
        viewModel.uiState.test {
            assertEquals(Idle, awaitItem())
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter when is loading more disabled`() = runTest {
        // Given:
        viewModel.isLoadingMoreEnabled = false
        val text = "123"
        every { filterUsersUseCase.execute(any()) } returns flow { emit(FAKE_USER_LIST) }

        // When:
        viewModel.filter(text)

        // Then:
        viewModel.uiState.test {
            assertEquals(Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `when filter throws an error this one is captured`() = runTest {
        // Given:
        val text = "123"
        val error = Exception("error")
        every { filterUsersUseCase.execute(any()) } returns flow { throw error }
        val expected = GlobalError(error)

        // When:
        viewModel.filter(text)

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun removeFilter() = runTest {
        // Given:
        every { removeUsersFilterUseCase.execute() } returns flow { emit(FAKE_USER_LIST) }
        val expected = FilterRemoved(FAKE_USER_LIST_ITEM)

        // When:
        viewModel.removeFilter()

        // Then:
        viewModel.uiState.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `when removeFilter throws an error this one is captured`() = runTest {
        // Given:
        val error = Exception("error")
        every { removeUsersFilterUseCase.execute() } returns flow { throw error }
        val expected = GlobalError(error)

        // When:
        viewModel.removeFilter()

        // Then:
        viewModel.event.test {
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}
