package com.r2b.apps.xuser.data.storage

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.data.storage.entity.UserRemoteKey
import com.r2b.apps.xuser.data.storage.room.CacheDatabase
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val cacheDatabase: CacheDatabase,
    private val userDAO: UserDAO,
    private val logger: Logger,
) : UserLocalDataSource {

    override fun pagingSource(query: String?): PagingSource<Int, UserDB> =
        userDAO.pagingSource(query?.takeIf { it.isNotBlank() }?.escapeLikeWildcards())

    override suspend fun nextPage(): Int? =
        cacheDatabase.userRemoteKeyDao().get()?.nextPage

    override suspend fun syncPage(users: List<UserDB>, refresh: Boolean, nextPage: Int?) {
        cacheDatabase.withTransaction {
            if (refresh) {
                if (users.isEmpty()) userDAO.clearActiveUsers()
                else userDAO.clearActiveUsersExcept(users.map(UserDB::serverId))
            }
            users.forEach { incoming ->
                val existing = userDAO.findByServerId(incoming.serverId)
                userDAO.update(
                    incoming.copy(
                        id = existing?.id,
                        deleted = existing?.deleted ?: false,
                    )
                )
            }
            cacheDatabase.userRemoteKeyDao().save(UserRemoteKey(nextPage = nextPage))
        }
    }

    override suspend fun get(id: Int): UserDB? {
        log("Request LOCAL id $id")
        val response = userDAO.read(id).firstOrNull()
        log("Response LOCAL id $id has ${if(response == null) "NOT " else ""}been found")
        return response
    }

    override suspend fun remove(id: Int) {
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

    private fun String.escapeLikeWildcards(): String =
        replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

    // endregion

}
