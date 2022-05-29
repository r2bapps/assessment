package com.r2b.apps.xuser.data.storage

import androidx.room.*
import com.r2b.apps.xuser.data.storage.entity.UserDB

@Dao
interface UserDAO: BaseDAO<UserDB> {

    /*
        REMEMBER:
        SQLite have no boolean data type
        Room maps boolean data to an INTEGER column
        Room mapping true to 1 and false to 0
    */

    @Query("SELECT * FROM User WHERE deleted = 0 ORDER BY id ASC LIMIT :items OFFSET :page")
    suspend fun page(page: Int, items: Int): List<UserDB>

    @Query("SELECT * FROM User WHERE id IN (:id) AND deleted = 0")
    suspend fun read(id: Int): List<UserDB>

    @Query("SELECT COUNT(id) FROM User")
    suspend fun count(): Int

}