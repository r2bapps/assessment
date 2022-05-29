package com.r2b.apps.xuser.data

import androidx.paging.LoadType
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.r2b.apps.utils.failureEither
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.storage.UserLocalDataSource
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.DomainError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediatorTest {

    @MockK
    private lateinit var remoteDataSource: UserDataSource

    @MockK(relaxed = true)
    private lateinit var localDataSource: UserLocalDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `initialize requests an initial refresh`() = runTest {
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertEquals(
            RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
            mediator.initialize(),
        )
    }

    @Test
    fun `refresh stores two full pages and schedules append`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { remoteDataSource.list(0) } returns successEither(users(1, 2))
        coEvery { remoteDataSource.list(1) } returns successEither(users(3, 2))
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertMediatorSuccess(result, endOfPaginationReached = false)
        coVerify {
            localDataSource.syncPage(
                match {
                    it.map(UserDB::serverId) == listOf("server-1", "server-2") &&
                        it.map(UserDB::sortOrder) == listOf(0, 1)
                },
                refresh = true,
                nextPage = 1,
            )
        }
        coVerify {
            localDataSource.syncPage(
                match {
                    it.map(UserDB::serverId) == listOf("server-3", "server-4") &&
                        it.map(UserDB::sortOrder) == listOf(2, 3)
                },
                refresh = false,
                nextPage = 2,
            )
        }
        coVerify(exactly = 1) { remoteDataSource.list(0) }
        coVerify(exactly = 1) { remoteDataSource.list(1) }
    }

    @Test
    fun `refresh stops after a short first page`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { remoteDataSource.list(0) } returns successEither(users(1, 1))
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertMediatorSuccess(
            mediator.load(LoadType.REFRESH, pagingState()),
            endOfPaginationReached = true,
        )

        coVerify(exactly = 1) { remoteDataSource.list(0) }
        coVerify(exactly = 0) { remoteDataSource.list(1) }
        coVerify(exactly = 1) { localDataSource.syncPage(any(), refresh = true, nextPage = null) }
    }

    @Test
    fun `refresh stops after a short second page`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { remoteDataSource.list(0) } returns successEither(users(1, 2))
        coEvery { remoteDataSource.list(1) } returns successEither(users(3, 1))
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertMediatorSuccess(
            mediator.load(LoadType.REFRESH, pagingState()),
            endOfPaginationReached = true,
        )

        coVerify(exactly = 1) { remoteDataSource.list(0) }
        coVerify(exactly = 1) { remoteDataSource.list(1) }
        coVerify {
            localDataSource.syncPage(
                match {
                    it.size == 1 && it.single().serverId == "server-3" && it.single().sortOrder == 2
                },
                refresh = false,
                nextPage = null,
            )
        }
    }

    @Test
    fun `append uses persisted next page and marks short responses complete`() = runTest {
        val user = UserDTO(
            serverId = "server-3",
            name = "User 3",
            picture = "picture-3",
        )
        every { remoteDataSource.pageItems } returns 2
        coEvery { localDataSource.nextPage() } returns 3
        coEvery { remoteDataSource.list(3) } returns successEither(listOf(user))
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        val result = mediator.load(LoadType.APPEND, pagingState())

        assertMediatorSuccess(result, endOfPaginationReached = true)
        coVerify {
            localDataSource.syncPage(
                match { it.single().serverId == "server-3" && it.single().sortOrder == 6 },
                refresh = false,
                nextPage = null,
            )
        }
    }

    @Test
    fun `append stores two full pages and advances the remote key`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { localDataSource.nextPage() } returns 3
        coEvery { remoteDataSource.list(3) } returns successEither(users(7, 2))
        coEvery { remoteDataSource.list(4) } returns successEither(users(9, 2))
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertMediatorSuccess(
            mediator.load(LoadType.APPEND, pagingState()),
            endOfPaginationReached = false,
        )

        coVerify {
            localDataSource.syncPage(
                match {
                    it.map(UserDB::serverId) == listOf("server-7", "server-8") &&
                        it.map(UserDB::sortOrder) == listOf(6, 7)
                },
                refresh = false,
                nextPage = 4,
            )
        }
        coVerify {
            localDataSource.syncPage(
                match {
                    it.map(UserDB::serverId) == listOf("server-9", "server-10") &&
                        it.map(UserDB::sortOrder) == listOf(8, 9)
                },
                refresh = false,
                nextPage = 5,
            )
        }
    }

    @Test
    fun `append keeps first page if second page request fails`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { localDataSource.nextPage() } returns 3
        coEvery { remoteDataSource.list(3) } returns successEither(users(7, 2))
        coEvery { remoteDataSource.list(4) } returns failureEither(
            ErrorDTO(code = 503, message = "Unavailable", cause = "upstream"),
        )
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        coVerify {
            localDataSource.syncPage(
                match { it.map(UserDB::serverId) == listOf("server-7", "server-8") },
                refresh = false,
                nextPage = 4,
            )
        }
    }

    @Test
    fun `refresh keeps first page if second page request fails`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { remoteDataSource.list(0) } returns successEither(users(1, 2))
        coEvery { remoteDataSource.list(1) } returns failureEither(
            ErrorDTO(code = 503, message = "Unavailable", cause = "upstream"),
        )
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        coVerify {
            localDataSource.syncPage(
                match { it.map(UserDB::serverId) == listOf("server-1", "server-2") },
                refresh = true,
                nextPage = 1,
            )
        }
    }

    @Test
    fun `prepend never requests a remote page or changes local data`() = runTest {
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertMediatorSuccess(
            mediator.load(LoadType.PREPEND, pagingState()),
            endOfPaginationReached = true,
        )

        coVerify(exactly = 0) { remoteDataSource.list(any()) }
        coVerify(exactly = 0) { localDataSource.syncPage(any(), any(), any()) }
    }

    @Test
    fun `append ends immediately when no next page is persisted`() = runTest {
        coEvery { localDataSource.nextPage() } returns null
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertMediatorSuccess(
            mediator.load(LoadType.APPEND, pagingState()),
            endOfPaginationReached = true,
        )

        coVerify(exactly = 0) { remoteDataSource.list(any()) }
        coVerify(exactly = 0) { localDataSource.syncPage(any(), any(), any()) }
    }

    @Test
    fun `refresh exposes API error and does not replace cached users`() = runTest {
        coEvery { remoteDataSource.list(0) } returns failureEither(
            ErrorDTO(code = 503, message = "Unavailable", cause = "upstream"),
        )
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        val error = (result as RemoteMediator.MediatorResult.Error).throwable
        assertTrue(error is UserRemoteMediator.UserSyncException)
        assertEquals(
            DomainError(code = 503, message = "Unavailable", cause = "upstream"),
            (error as UserRemoteMediator.UserSyncException).domainError,
        )
        assertEquals("Error 503: Unavailable: upstream", error.message)
        coVerify(exactly = 0) { localDataSource.syncPage(any(), any(), any()) }
    }

    @Test
    fun `empty refresh response clears the page and marks the end`() = runTest {
        every { remoteDataSource.pageItems } returns 2
        coEvery { remoteDataSource.list(0) } returns successEither(emptyList())
        val mediator = UserRemoteMediator(remoteDataSource, localDataSource)

        assertMediatorSuccess(
            mediator.load(LoadType.REFRESH, pagingState()),
            endOfPaginationReached = true,
        )

        coVerify(exactly = 1) {
            localDataSource.syncPage(emptyList(), refresh = true, nextPage = null)
        }
    }

    private fun pagingState() = PagingState<Int, UserDB>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 2),
        leadingPlaceholderCount = 0,
    )

    private fun users(firstId: Int, count: Int): List<UserDTO> =
        (firstId until firstId + count).map { id ->
            UserDTO(
                serverId = "server-$id",
                name = "User $id",
                picture = "picture-$id",
            )
        }

    private fun assertMediatorSuccess(
        result: RemoteMediator.MediatorResult,
        endOfPaginationReached: Boolean,
    ) {
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(
            endOfPaginationReached,
            (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached,
        )
    }
}
