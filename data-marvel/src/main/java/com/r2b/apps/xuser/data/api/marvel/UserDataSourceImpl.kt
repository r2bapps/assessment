package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.marvel.retrofit.MarvelService
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.api.entity.UserDTO

typealias Result = com.r2b.apps.lib.api.marvel.entity.Result
typealias Data = com.r2b.apps.lib.api.marvel.entity.Data

class UserDataSourceImpl(
    private val marvelUserService: MarvelService,
    private val logger: Logger,
    override val pageItems: Int,
): UserDataSource {

    init {
        log("Defined page items $pageItems")
    }

    override suspend fun list(page: Int): List<UserDTO> {
        log("Request page $page")
        val response = marvelUserService.characters(
            limit = pageItems,
            offset = page * pageItems,
        )
        log("Response " + response.data?.getPage().toString())
        return response.data?.results.toDTO()
    }

    // region Utils

    private fun log(message: String) {
        logger.d("${this.javaClass.simpleName}: $message")
    }

    // endregion

}
