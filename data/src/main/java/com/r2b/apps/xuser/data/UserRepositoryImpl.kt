package com.r2b.apps.xuser.data

import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.filter.UserFilterDelegate
import com.r2b.apps.xuser.data.storage.UserLocalDataSourceImpl
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.repository.UserRepository
import com.r2b.apps.xuser.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSourceImpl,
    private val userFilterDelegate: UserFilterDelegate,
    private val logger: Logger,
) : UserRepository {

    private var currentUserId: Int? = null
    private var currentList: MutableList<User> = mutableListOf()

    override suspend fun list(page: Int): List<User> {
        var cached: List<User> = userLocalDataSource.list(page).dbEntityToDomain()
        if (cached.isEmpty()) {
            val response: List<UserDB> = userDataSource.list(page).dtoToDBEntity()
            userLocalDataSource.create(response)
            cached = userLocalDataSource.list(page).dbEntityToDomain()
        }
        currentList.addAll(cached)
        return cached
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

    //region Utils

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    // endregion

}
