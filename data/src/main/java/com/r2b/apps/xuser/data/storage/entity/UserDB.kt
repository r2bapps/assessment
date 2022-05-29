package com.r2b.apps.xuser.data.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "User", indices = [Index(value = ["serverId"], unique = true)] )
data class UserDB(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "serverId") val serverId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String? = "",
    @ColumnInfo(name = "email") val email: String? = "",
    @ColumnInfo(name = "phone") val phone: String? = "",
    @ColumnInfo(name = "picture") val picture: String,
    @ColumnInfo(name = "gender") val gender: String? = "",
    @ColumnInfo(name = "location") val location: String? = "",
    @ColumnInfo(name = "registeredDate") val registeredDate: String? = "",
    @ColumnInfo(name = "description") val description: String? = "",
    @ColumnInfo(name = "deleted") val deleted: Boolean = false,
)

