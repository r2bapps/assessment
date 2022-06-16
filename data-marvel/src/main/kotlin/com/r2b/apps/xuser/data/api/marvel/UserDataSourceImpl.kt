package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.getOrDefaultOrNull
import com.r2b.apps.lib.api.marvel.retrofit.MarvelService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.utils.failureEither
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.DTOResponse
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.toErrorDTO
import java.io.IOException

typealias Result = com.r2b.apps.lib.api.marvel.entity.Result
typealias Data = com.r2b.apps.lib.api.marvel.entity.Data

class UserDataSourceImpl(
    private val marvelUserService: MarvelService,
    private val logger: Logger,
    override val pageItems: Int,
    override val retry: Boolean,
): UserDataSource {

    init {
        log("Defined page items $pageItems")
    }

    override suspend fun list(page: Int): DTOResponse<List<UserDTO>> {
        log("Request page $page")
        // TODO: retry
        val response = getCharacters(page)

        if (response.isFailure()) {
            response.getFailureOrNull()?.let { log(it) }
        } else {
            log("Response " + response.getOrDefaultOrNull()?.data?.getPage().toString())
        }

        return when (response) {
            is NetworkResponse.Success -> successEither(response.body.toDTO())
            is NetworkResponse.ApiError -> failureEither(response.body.toDTO(response.code))
            is NetworkResponse.NetworkError -> failureEither((response.getFailureOrNull()!! as IOException).toErrorDTO())
            is NetworkResponse.UnknownError -> failureEither(response.getFailureOrNull()!!.toErrorDTO())
        }
    }

    private suspend fun getCharacters(page: Int) =
        marvelUserService.characters(
            limit = pageItems,
            offset = page * pageItems,
        )

    // region Utils

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    private fun log(error: Throwable) {
        logger.d("${this.javaClass.simpleName}: $error")
    }

    // endregion

}
