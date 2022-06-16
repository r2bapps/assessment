package com.r2b.apps.xuser.data

import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.utils.getOrDefaultOrNull
import com.r2b.apps.utils.map
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.filter.UserFilterDelegate
import com.r2b.apps.xuser.data.storage.UserLocalDataSource
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.DomainResponse
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val userFilterDelegate: UserFilterDelegate,
    private val logger: Logger,
) : UserRepository {

    private var currentUserId: Int? = null
    private var currentList: MutableList<User> = mutableListOf()

    override suspend fun list(page: Int): DomainResponse<List<User>> {
        var cached: List<User> = userLocalDataSource.list(page).dbEntityToDomain()
        if (cached.isEmpty()) {
            val response = userDataSource.list(page)
            response.getOrDefaultOrNull()?.let {
                cached = saveAndRetrieveFromLocal(it, page)
                currentList.addAll(cached)
            }
            return response.map(
                { cached },
                { it.toDomainError() }
            )
        }
        currentList.addAll(cached)
        return successEither(cached)
    }

    override suspend fun setCurrentUser(id: Int) {
        log("Request set current user id $id")
        currentUserId = id
    }

    override suspend fun getCurrentUser(): User? {
        log("Request current user id $currentUserId")
        val response = userLocalDataSource.get(currentUserId!!)?.dbEntityToDomain()
        log("Response current with id ${response?.id}")
        return response
    }

    override suspend fun removeUser(id: Int) {
        log("Request remove id $currentUserId")
        val response = userLocalDataSource.remove(id)
        return response
    }

    override suspend fun filterUsersBy(text: String): List<User> {
        log("Request filter by $text")
        val response = userFilterDelegate.filterBy(currentList, text)
        log("Response filter by ${text} has found ${response.size} items")
        return response
    }

    override suspend fun removeFilter(): List<User> = currentList

    private suspend fun saveAndRetrieveFromLocal(users: List<UserDTO>, page: Int): List<User> {
        val dbEntityList: List<UserDB> = users.dtoToDBEntity()
        userLocalDataSource.create(dbEntityList)
        return userLocalDataSource.list(page).dbEntityToDomain()
    }

    //region Utils

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    // endregion

}
