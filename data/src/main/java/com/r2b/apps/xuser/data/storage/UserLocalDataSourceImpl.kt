package com.r2b.apps.xuser.data.storage

import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.storage.entity.UserDB
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val userDAO: UserDAO,
) {

    @Inject lateinit var logger: Logger

    // TODO: Move Pagination to a shared place and use on pageItems
    private val pageItems = 20

    suspend fun list(page: Int): List<UserDB> {
        log("Request LOCAL page $page with items $pageItems")
        return userDAO.page(page, pageItems)
    }

    suspend fun create(dbEntityList: List<UserDB>) {
        log("Request LOCAL bulk save ${dbEntityList.size} items")
        val oldCountItems = userDAO.count()
        userDAO.create(*dbEntityList.toTypedArray())
        val newCountItems = userDAO.count()
        log("Response LOCAL added ${newCountItems-oldCountItems} items more")
    }

    suspend fun get(id: Int): UserDB? {
        log("Request LOCAL id $id")
        val response = userDAO.read(id).firstOrNull()
        log("Response LOCAL id $id has ${if(response == null) "NOT " else ""}been found")
        return response
    }

    suspend fun remove(id: Int) {
        log("Request remove (logical delete) LOCAL id $id")
        val response = userDAO.read(id).firstOrNull()
        response?.let {
            userDAO.update( it.copy(deleted = true) )
        }
        log("Response remove (logical delete) LOCAL id $id has ${if(response == null) "NOT " else ""}been found")
    }

    // region Utils

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    // endregion

}