package com.r2b.apps.xuser.data.storage

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.data.storage.room.CacheDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserLocalDataSourceSyncTest {

    private lateinit var database: CacheDatabase
    private lateinit var userDAO: UserDAO
    private lateinit var localDataSource: UserLocalDataSourceImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            CacheDatabase::class.java,
        ).allowMainThreadQueries().build()
        userDAO = database.userDao()
        localDataSource = UserLocalDataSourceImpl(database, userDAO, NoOpLogger)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refreshReplacesActiveCachePreservesTombstonesAndStoresNextPage() = runBlocking {
        val deletedUser = UserDB(
            id = 1,
            serverId = "deleted-user",
            name = "Deleted",
            picture = "deleted-picture",
            deleted = true,
        )
        val staleUser = UserDB(
            id = 2,
            serverId = "stale-user",
            name = "Stale",
            picture = "stale-picture",
        )
        val refreshedUser = UserDB(
            id = 3,
            serverId = "refreshed-user",
            name = "Old name",
            picture = "old-picture",
        )
        userDAO.create(deletedUser, staleUser, refreshedUser)

        localDataSource.syncPage(
            users = listOf(
                deletedUser.copy(id = null, name = "Should stay deleted"),
                refreshedUser.copy(id = null, name = "Updated name"),
                UserDB(serverId = "fresh-user", name = "Fresh", picture = "fresh-picture"),
            ),
            refresh = true,
            nextPage = 1,
        )

        val result = userDAO.pagingSource(query = null).load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        assertEquals(listOf("refreshed-user", "fresh-user"), result.data.map(UserDB::serverId))
        assertEquals(3, userDAO.findByServerId("refreshed-user")!!.id)
        assertEquals("Updated name", userDAO.findByServerId("refreshed-user")!!.name)
        assertTrue(userDAO.findByServerId("deleted-user")!!.deleted)
        assertEquals(1, database.userRemoteKeyDao().get()!!.nextPage)
    }

    private object NoOpLogger : Logger {
        override fun init(debugMode: Boolean) = Unit
        override fun d(message: String) = Unit
        override fun d(throwable: Throwable) = Unit
    }
}
