package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.getOrDefaultOrNull
import com.r2b.apps.lib.api.randomuser.retrofit.RandomUserService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.utils.failureEither
import com.r2b.apps.utils.successEither
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.DTOResponse
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.toErrorDTO
import java.io.IOException

class UserDataSourceImpl (
    private val randomUserService: RandomUserService,
    private val logger: Logger,
    override val pageItems: Int,
    override val retry: Boolean,
): UserDataSource {

    init {
        log("Defined page items $pageItems")
    }

    override suspend fun list(page: Int): DTOResponse<List<UserDTO>> {
        log("Request page $page")
        // first page is page 1
        // TODO: retry
        val response = getUsers(page)

        if (response.isFailure()) {
            response.getFailureOrNull()?.let { log(it) }
        } else {
            log("Response " + response.getOrDefaultOrNull()?.info?.getPage().toString())
        }

        return when (response) {
            is NetworkResponse.Success -> successEither(response.body.toDTO())
            is NetworkResponse.ApiError -> failureEither(response.body.toDTO(response.code))
            is NetworkResponse.NetworkError -> failureEither((response.getFailureOrNull()!! as IOException).toErrorDTO())
            is NetworkResponse.UnknownError -> failureEither(response.getFailureOrNull()!!.toErrorDTO())
        }
    }

    private suspend fun getUsers(page: Int) =
        randomUserService.users(
            page = page + 1,
            results = pageItems,
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