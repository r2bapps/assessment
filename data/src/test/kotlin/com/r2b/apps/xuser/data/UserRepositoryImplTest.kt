package com.r2b.apps.xuser.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.storage.UserLocalDataSource
import com.r2b.apps.xuser.data.storage.entity.UserDB
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UserRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var remoteDataSource: UserDataSource

    @MockK
    lateinit var localDataSource: UserLocalDataSource

    @Test
    fun `list pages local entities as domain users and starts remote refresh`() = runTest {
        val entity = testUserEntity(id = 1, serverId = "remote-1", name = "Ada")
        every { remoteDataSource.pageItems } returns 20
        every { localDataSource.pagingSource(null) } returns ListPagingSource(listOf(entity))
        coEvery { remoteDataSource.list(0) } returns successEither(emptyList())
        coEvery { localDataSource.syncPage(any(), any(), any()) } returns Unit
        val repository = UserRepositoryImpl(remoteDataSource, localDataSource)

        assertEquals(listOf(entity.dbEntityToDomain()), repository.list().asSnapshot())
        coVerify(exactly = 1) { remoteDataSource.list(0) }
        coVerify(exactly = 1) { localDataSource.syncPage(emptyList(), refresh = true, nextPage = null) }
    }

    @Test
    fun `filter uses local paging source and does not request the remote API`() = runTest {
        val query = "Ada"
        val entity = testUserEntity(id = 7, serverId = "remote-7", name = "Ada Lovelace")
        every { remoteDataSource.pageItems } returns 20
        every { localDataSource.pagingSource(query) } returns ListPagingSource(listOf(entity))
        val repository = UserRepositoryImpl(remoteDataSource, localDataSource)

        assertEquals(listOf(entity.dbEntityToDomain()), repository.filterUsersBy(query).asSnapshot())
        coVerify(exactly = 0) { remoteDataSource.list(any()) }
        coVerify(exactly = 0) { localDataSource.syncPage(any(), any(), any()) }
    }

    @Test
    fun `get user converts the requested persisted entity`() = runTest {
        val entity = testUserEntity(id = 12, serverId = "remote-12", name = "Grace")
        coEvery { localDataSource.get(12) } returns entity
        val repository = UserRepositoryImpl(remoteDataSource, localDataSource)

        assertEquals(entity.dbEntityToDomain(), repository.getUser(12))
        coVerify(exactly = 1) { localDataSource.get(12) }
    }

    @Test
    fun `get user returns null for an absent entity`() = runTest {
        coEvery { localDataSource.get(404) } returns null
        val repository = UserRepositoryImpl(remoteDataSource, localDataSource)

        assertEquals(null, repository.getUser(404))
    }

    @Test
    fun `remove delegates the requested local id`() = runTest {
        coEvery { localDataSource.remove(9) } returns Unit
        val repository = UserRepositoryImpl(remoteDataSource, localDataSource)

        repository.removeUser(9)

        coVerify(exactly = 1) { localDataSource.remove(9) }
    }

    private fun testUserEntity(id: Int, serverId: String, name: String) = UserDB(
        id = id,
        serverId = serverId,
        name = name,
        picture = "",
    )

    private class ListPagingSource(
        private val users: List<UserDB>,
    ) : PagingSource<Int, UserDB>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserDB> {
            val start = params.key ?: 0
            val page = users.drop(start).take(params.loadSize)
            val previousKey = if (start == 0) null else (start - params.loadSize).coerceAtLeast(0)
            val nextKey = (start + page.size).takeIf { it < users.size }
            return LoadResult.Page(
                data = page,
                prevKey = previousKey,
                nextKey = nextKey,
            )
        }

        override fun getRefreshKey(state: PagingState<Int, UserDB>): Int? = null
    }
}
