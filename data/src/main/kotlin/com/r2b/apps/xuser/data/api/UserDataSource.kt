package com.r2b.apps.xuser.data.api

import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.entity.DTOResponse
import com.r2b.apps.xuser.data.api.entity.UserDTO

interface UserDataSource {
    val pageItems: Int
    val retry: Boolean
    suspend fun list(page: Int): DTOResponse<List<UserDTO>> = successEither(emptyList())
}