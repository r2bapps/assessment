package com.r2b.apps.xuser.data.api

import com.r2b.apps.xuser.data.api.entity.UserDTO

interface UserDataSource {
    val pageItems: Int
    suspend fun list(page: Int): List<UserDTO> = emptyList()
}