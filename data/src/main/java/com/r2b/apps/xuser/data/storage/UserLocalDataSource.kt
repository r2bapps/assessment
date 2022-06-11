package com.r2b.apps.xuser.data.storage

import com.r2b.apps.xuser.data.storage.entity.UserDB

interface UserLocalDataSource {
    suspend fun list(page: Int): List<UserDB>
    suspend fun create(dbEntityList: List<UserDB>)
    suspend fun get(id: Int): UserDB?
    suspend fun remove(id: Int)
}
