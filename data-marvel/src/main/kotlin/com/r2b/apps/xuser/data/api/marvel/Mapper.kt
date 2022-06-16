package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.lib.api.marvel.entity.MarvelCharacterResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelErrorResponse
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.switchToHttps
import com.r2b.apps.xuser.data.toIntOrDefault

internal fun MarvelCharacterResponse.toDTO(): List<UserDTO> =
    this.data?.results.toDTO()

internal fun MarvelErrorResponse.toDTO(code: Int): ErrorDTO =
    ErrorDTO(code = this.code ?: code, message = this.status.orEmpty(), cause = "")

internal fun List<Result>?.toDTO(): List<UserDTO> =
    this?.map { it.toDTO() } ?: emptyList()

internal fun Result.toDTO(): UserDTO =
    UserDTO(
        serverId = id.toString(),
        name = name.orEmpty(),
        picture = if (thumbnail?.path == null) "" else "${switchToHttps(thumbnail?.path.orEmpty())}.${thumbnail?.extension.orEmpty()}",
        description = description.orEmpty()
    )

internal fun Data.getPage(): Page =
    Page(
        limit.toIntOrDefault(),
        count.toIntOrDefault(),
        offset.toIntOrDefault(),
        total.toIntOrDefault()
    )

// TODO: allow pagination configuration
// TODO: avoid HTTP call on next pages from last page
internal data class Page(
    val limit: Int,
    val count: Int,
    val offset: Int,
    val total: Int,
    val isLastPage: Boolean = count < limit
)