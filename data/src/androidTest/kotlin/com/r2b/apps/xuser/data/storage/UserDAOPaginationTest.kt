package com.r2b.apps.xuser.data.storage

import android.content.Context
import androidx.room.Room
import androidx.paging.PagingSource
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.data.storage.room.CacheDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDAOPaginationTest {

    private lateinit var database: CacheDatabase
    private lateinit var userDAO: UserDAO

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            CacheDatabase::class.java,
        ).allowMainThreadQueries().build()
        userDAO = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun pagingSourceReturnsOrderedPagesAndSkipsLogicallyDeletedUsers() = runBlocking {
        val users = (1..45).map { id ->
            UserDB(
                id = id,
                serverId = "server-$id",
                name = "User $id",
                picture = "picture-$id",
                deleted = id % 5 == 0,
            )
        }
        userDAO.create(*users.toTypedArray())
        val activeUsers = users.filterNot { it.deleted }

        val pagingSource = userDAO.pagingSource(query = null)
        val firstPage = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        val secondPage = pagingSource.load(
            PagingSource.LoadParams.Append(key = firstPage.nextKey!!, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals(activeUsers.take(20), firstPage.data)
        assertEquals(activeUsers.drop(20), secondPage.data)
        assertFalse(firstPage.data.any { it.deleted })
        assertEquals(null, secondPage.nextKey)
    }
}
