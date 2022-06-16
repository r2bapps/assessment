package com.r2b.apps.xuser.data.api.entity

import com.r2b.apps.utils.Either

data class ErrorDTO(
    val code: Int? = 0,
    val message: String? = "",
    val cause: String? = "",
)

typealias DTOResponse<T> = Either<T, ErrorDTO>
