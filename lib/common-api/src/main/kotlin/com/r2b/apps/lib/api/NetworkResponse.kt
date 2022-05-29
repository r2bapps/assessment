package com.r2b.apps.lib.api

import com.r2b.apps.lib.api.NetworkResponse.*
import java.io.IOException

sealed class NetworkResponse<out T, out U> {
    /**
     * Success response with body
     */
    data class Success<T>(val body: T) : NetworkResponse<T, Nothing>()

    /**
     * Failure response with body
     */
    data class ApiError<U>(val body: U, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable) : NetworkResponse<Nothing, Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isApiError(): Boolean = this is ApiError
    fun isNetworkError(): Boolean = this is NetworkError
    fun isUnknownError(): Boolean = this is UnknownError
    fun isFailure(): Boolean = (this is Success).not()
    fun getFailureOrNull(): Throwable? =
        when (this) {
            is Success -> null
            is ApiError -> null
            is NetworkError -> error
            is UnknownError -> error
        }
}

fun <T> successNetworkResponse(body: T) = Success(body)

fun <T, U> NetworkResponse<T, U>.getOrDefaultOrNull(default: T? = null): T? =
    if (this is Success) body else default

fun <T, T2, U, U2> NetworkResponse<T, U>.map(
    onSuccess: (T) -> T2,
    onApiError: (U, Int) -> U2
): NetworkResponse<T2, U2> =
    when(this) {
        is Success -> Success(onSuccess(body))
        is ApiError -> ApiError(onApiError(body, code), code)
        is NetworkError -> NetworkError(error)
        is UnknownError -> UnknownError(error)
    }
