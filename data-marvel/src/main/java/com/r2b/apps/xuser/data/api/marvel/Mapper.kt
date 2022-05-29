package com.r2b.apps.xuser.data.api.marvel

import com.r2b.apps.xuser.data.api.entity.UserDTO

internal fun List<Result>?.toDTO(): List<UserDTO> =
    this?.map { it.toDTO() } ?: emptyList()

// Starting with Android 9 (API level 28), cleartext support is disabled by default.
internal fun switchToHttps(url: String): String =
    if (url.contains("https")) url else url.replace("http", "https")

internal fun Result.toDTO(): UserDTO =
    UserDTO(
        serverId = id.toString(),
        name = name.orEmpty(),
        picture = if (thumbnail?.path == null) "" else "${switchToHttps(thumbnail?.path.orEmpty())}.${thumbnail?.extension.orEmpty()}",
        description = description.orEmpty()
    )

internal fun String?.toIntOrDefault(default: Int = 0) =
    this?.toInt() ?: default

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