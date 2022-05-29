package com.r2b.apps.xuser.domain.repository

import androidx.paging.PagingData
import com.r2b.apps.xuser.domain.model.User
import kotlinx.coroutines.flow.Flow

// TODO: Avoid the use of suspend keyword (on all dependencies)

interface UserRepository {
    fun list(): Flow<PagingData<User>>
    suspend fun getUser(id: Int): User?
    suspend fun removeUser(id: Int)
    fun filterUsersBy(text: String): Flow<PagingData<User>>
}