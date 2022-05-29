package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.lib.api.randomuser.retrofit.RandomUserService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.UserDTO

class UserDataSourceImpl (
    private val randomUserService: RandomUserService,
    private val logger: Logger,
    override val pageItems: Int,
): UserDataSource {

    init {
        log("Defined page items $pageItems")
    }

    override suspend fun list(page: Int): List<UserDTO> {
        log("Request page $page")
        // first page is page 1
        val response = randomUserService.users(
            page = page + 1,
            results = pageItems,
        )
        log("Response " + response.info?.getPage().toString())
        return response.results.toDTO()
    }

    // region Utils

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    // endregion


}