package com.r2b.apps.xuser.domain.model

import com.r2b.apps.utils.Either

data class DomainError(
    val code: Int = 0,
    val message: String = "",
    val cause: String = "",
)

typealias DomainResponse<T> = Either<T, DomainError>