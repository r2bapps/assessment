package com.r2b.apps.utils

import com.r2b.apps.utils.Either.Success
import com.r2b.apps.utils.Either.Failure

sealed class Either<out T, out U> {
    data class Success<out T>(val data: T) : Either<T, Nothing>()
    data class Failure<out U>(val error: U) : Either<Nothing, U>()

    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
    fun emptySuccessEither() = Success(Unit)
}

fun <T> successEither(data: T) = Success(data)

fun <U> failureEither(error: U) = Failure(error)

fun <T, U> Either<T, U>.getOrDefaultOrNull(default: T? = null): T? =
    if (this is Success) data else default

fun <T, T2, U, U2> Either<T, U>.map(
    onSuccess: (T) -> T2,
    onFailure: (U) -> U2
): Either<T2, U2> =
    when(this) {
        is Success -> Success(onSuccess(data))
        is Failure -> Failure(onFailure(error))
    }
