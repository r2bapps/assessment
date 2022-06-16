package com.r2b.apps.xuser.data.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.r2b.apps.xuser.data.storage.UserDAO
import com.r2b.apps.xuser.data.storage.entity.UserDB

@Database(entities = [
    UserDB::class,
], version = 1)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun userDao(): UserDAO
}
