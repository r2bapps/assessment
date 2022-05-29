package com.r2b.apps.xuser.data

import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.filter.UserFilterDelegate
import com.r2b.apps.xuser.data.storage.UserLocalDataSourceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl

    @MockK lateinit var logger: Logger
    @MockK lateinit var filterDelegate: UserFilterDelegate
    @MockK lateinit var dataSource: UserDataSource
    @MockK lateinit var localDataSource: UserLocalDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = UserRepositoryImpl(dataSource, localDataSource)
        repository.logger = logger
        repository.userFilterDelegate = filterDelegate
    }

    @After
    fun tearDown() {
    }

    @Test
    fun listNotCached() = runBlocking {
        every { runBlocking { localDataSource.list(any()) } } returns emptyList() andThen FAKE_USER_DB_LIST
        every { runBlocking { dataSource.list(any()) } } returns FAKE_USER_DTO_LIST

        val page = 0
        val expected = FAKE_USER_LIST
        val response = repository.list(page)

        assertEquals(expected, response)
        verify { runBlocking { localDataSource.create(any()) } }
        verify(atLeast = 2) { runBlocking { localDataSource.list(any()) } }
        verify{ runBlocking { dataSource.list(any()) } }
    }

    @Test
    fun listCached() = runBlocking {
        val page = 0
        val expected = FAKE_USER_LIST
        every { runBlocking { localDataSource.list(any()) } } returns FAKE_USER_DB_LIST

        val response = repository.list(page)

        assertEquals(expected, response)
        verify(exactly = 0) { runBlocking { localDataSource.create(any()) } }
        verify(exactly = 1) { runBlocking { localDataSource.list(any()) } }
    }

    @Test
    fun removeUser() = runBlocking {
        val id = 0
        repository.removeUser(id)
        verify { runBlocking { localDataSource.remove(id) } }
    }

}