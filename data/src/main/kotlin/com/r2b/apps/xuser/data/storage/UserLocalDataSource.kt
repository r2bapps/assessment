package com.r2b.apps.xuser.data.storage

import androidx.paging.PagingSource
import com.r2b.apps.xuser.data.storage.entity.UserDB

interface UserLocalDataSource {
    fun pagingSource(query: String? = null): PagingSource<Int, UserDB>
    suspend fun nextPage(): Int?
    suspend fun syncPage(users: List<UserDB>, refresh: Boolean, nextPage: Int?)
    suspend fun get(id: Int): UserDB?
    suspend fun remove(id: Int)
}
