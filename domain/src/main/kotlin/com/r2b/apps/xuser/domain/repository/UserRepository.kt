package com.r2b.apps.xuser.domain.repository

import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.domain.model.DomainResponse
import com.r2b.apps.xuser.domain.model.User

// TODO: Avoid the use of suspend keyword (on all dependencies)

interface UserRepository {
    suspend fun list(page: Int): DomainResponse<List<User>> = successEither(emptyList())
    suspend fun setCurrentUser(id: Int)
    suspend fun getCurrentUser(): User?
    suspend fun removeUser(id: Int)
    suspend fun filterUsersBy(text: String): List<User>
    suspend fun removeFilter(): List<User>
}