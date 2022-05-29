package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiErrorResponse
import com.r2b.apps.lib.api.marvel.retrofit.MarvelDatabaseService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.utils.failureEither
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.DTOResponse
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.toErrorDTO

class UserDataSourceImpl(
    private val marvelDatabaseService: MarvelDatabaseService,
    private val logger: Logger,
    override val pageItems: Int,
    override val retry: Boolean,
) : UserDataSource {

    init {
        log("Defined page items $pageItems")
    }

    override suspend fun list(page: Int): DTOResponse<List<UserDTO>> {
        log("Request page $page")
        val response = marvelDatabaseService.characters(
            limit = pageItems,
            offset = page * pageItems,
        )

        return when (response) {
            is NetworkResponse.Success -> {
                val apiError = response.body.error
                if (apiError != null) {
                    log(apiError.info.orEmpty())
                    failureEither(MarvelDatabaseApiErrorResponse(apiError).toDTO())
                } else {
                    val users = response.body.toDTO()
                    log("Received ${users.size} character pages")
                    successEither(users)
                }
            }

            is NetworkResponse.ApiError -> {
                log("HTTP ${response.code}: ${response.body.error?.info.orEmpty()}")
                failureEither(response.body.toDTO(response.code))
            }

            is NetworkResponse.NetworkError -> {
                log(response.error)
                failureEither(response.error.toErrorDTO())
            }

            is NetworkResponse.UnknownError -> {
                log(response.error)
                failureEither(response.error.toErrorDTO())
            }
        }
    }

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    private fun log(error: Throwable) {
        logger.d("${this.javaClass.simpleName}: $error")
    }
}
