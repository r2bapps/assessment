package com.r2b.apps.xuser.data.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.r2b.apps.xuser.data.storage.UserDAO
import com.r2b.apps.xuser.data.storage.UserRemoteKeyDAO
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.data.storage.entity.UserRemoteKey

@Database(entities = [
    UserDB::class,
    UserRemoteKey::class,
], version = 2)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun userRemoteKeyDao(): UserRemoteKeyDAO
}
