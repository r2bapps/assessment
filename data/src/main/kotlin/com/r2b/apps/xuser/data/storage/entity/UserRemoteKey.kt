package com.r2b.apps.xuser.data.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserRemoteKey")
data class UserRemoteKey(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = USER_LIST_KEY,
    @ColumnInfo(name = "nextPage") val nextPage: Int?,
)

const val USER_LIST_KEY = "user_list"
