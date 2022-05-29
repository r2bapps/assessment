package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.lib.api.randomuser.entity.Info
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.lib.api.randomuser.entity.Results
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

internal fun List<Results>?.toDTO(): List<UserDTO> =
    this?.map { it.toDTO() } ?: emptyList()

internal fun Info.getPage(): Page =
    Page(
        page ?: 0,
        results ?: 0,
        seed ?: "not found",
    )

private fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

// TODO: Repeated users, Unexpetedly!!
internal fun Results.generateId(): String =
    md5(
        name?.first.orEmpty().lowercase().trim() +
        name?.last.orEmpty().lowercase().trim() +
        email.orEmpty().lowercase().trim() +
        phone.orEmpty().trim()
    )

internal fun Results.toDTO(): UserDTO =
    UserDTO(
        serverId = generateId(),
        name = name?.first.orEmpty().trim(),
        surname = name?.last.orEmpty().trim(),
        email = email.orEmpty().trim(),
        phone = phone.orEmpty().trim(),
        picture = switchToHttps(picture?.large.orEmpty().trim()),
        gender = gender.orEmpty().trim(),
        location = if (location?.street?.name == null) "" else "${location?.street?.name.orEmpty().trim()}, ${location?.street?.number.toString().trim()}",
        registeredDate = registered?.date.orEmpty().trim(),
        description = "",
    )

// Starting with Android 9 (API level 28), cleartext support is disabled by default.
internal fun switchToHttps(url: String): String =
    if (url.contains("https", ignoreCase = true)) url else url.replace("http", "https")


// TODO: allow pagination configuration
// TODO: avoid HTTP call on next pages from last page
internal data class Page(
    val page: Int,
    val results: Int,
    val seed: String,
)