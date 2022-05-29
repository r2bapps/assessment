package com.r2b.apps.xuser.data.storage

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDAO<T> {

    // region CRUD Templates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(dbEntity: T)

    // TODO: BaseDAO generic read method
    /*@Query("SELECT * FROM User WHERE id IN (:id)")
    suspend fun read(id: Int): List<T>*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(dbEntity: T)

    @Delete
    suspend fun delete(dbEntity: T)

    // endregion

    //region Bulk CRUD Templates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(vararg list: T)

    // TODO: BaseDAO generic bulk read method
    /*@Query("SELECT * FROM User")
    suspend fun read(): List<T>*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg list: T)

    @Delete
    suspend fun delete(vararg list: T)

    // endregion

}