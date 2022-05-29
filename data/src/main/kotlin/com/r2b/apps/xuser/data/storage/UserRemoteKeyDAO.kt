package com.r2b.apps.xuser.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.r2b.apps.xuser.data.storage.entity.USER_LIST_KEY
import com.r2b.apps.xuser.data.storage.entity.UserRemoteKey

@Dao
interface UserRemoteKeyDAO {

    @Query("SELECT * FROM UserRemoteKey WHERE id = :id")
    suspend fun get(id: String = USER_LIST_KEY): UserRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(remoteKey: UserRemoteKey)
}
