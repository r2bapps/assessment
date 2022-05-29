package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiErrorResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabasePage
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseResponse
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.switchToHttps

internal fun MarvelDatabaseResponse.toDTO(): List<UserDTO> =
    query?.pages.orEmpty()
        .sortedBy { it.index ?: Int.MAX_VALUE }
        .map(MarvelDatabasePage::toDTO)

internal fun MarvelDatabaseApiErrorResponse.toDTO(httpCode: Int? = null): ErrorDTO =
    ErrorDTO(
        code = httpCode ?: 0,
        message = error?.info ?: "Marvel Database request failed",
        cause = error?.code.orEmpty(),
    )

private fun MarvelDatabasePage.toDTO(): UserDTO =
    UserDTO(
        serverId = pageId?.toString().orEmpty(),
        name = title.orEmpty(),
        picture = thumbnail?.source?.let(::switchToHttps).orEmpty(),
        description = fullUrl?.let { "Marvel Database: $it" }.orEmpty(),
    )
