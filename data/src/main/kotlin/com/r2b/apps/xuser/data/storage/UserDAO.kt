package com.r2b.apps.xuser.data.storage

import androidx.room.*
import androidx.paging.PagingSource
import com.r2b.apps.xuser.data.storage.entity.UserDB

@Dao
interface UserDAO: BaseDAO<UserDB> {

    /*
        REMEMBER:
        SQLite have no boolean data type
        Room maps boolean data to an INTEGER column
        Room mapping true to 1 and false to 0
    */

    @Query(
        """
        SELECT * FROM User
        WHERE deleted = 0
          AND (:query IS NULL
            OR name LIKE :query || '%' ESCAPE '\' COLLATE NOCASE
            OR surname LIKE :query || '%' ESCAPE '\' COLLATE NOCASE
            OR email LIKE :query || '%' ESCAPE '\' COLLATE NOCASE)
        ORDER BY sortOrder ASC, id ASC
        """
    )
    fun pagingSource(query: String?): PagingSource<Int, UserDB>

    @Query("SELECT * FROM User WHERE serverId = :serverId LIMIT 1")
    suspend fun findByServerId(serverId: String): UserDB?

    @Query("DELETE FROM User WHERE deleted = 0")
    suspend fun clearActiveUsers()

    @Query("DELETE FROM User WHERE deleted = 0 AND serverId NOT IN (:serverIds)")
    suspend fun clearActiveUsersExcept(serverIds: List<String>)

    @Query("SELECT * FROM User WHERE id IN (:id) AND deleted = 0")
    suspend fun read(id: Int): List<UserDB>

}