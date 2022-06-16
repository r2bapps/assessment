package com.r2b.apps.xuser.data.api.randomuser

import com.r2b.apps.lib.api.randomuser.entity.Info
import com.r2b.apps.lib.api.randomuser.entity.RandomUserErrorResponse
import com.r2b.apps.lib.api.randomuser.entity.RandomUserResponse
import com.r2b.apps.lib.api.randomuser.entity.Results
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.switchToHttps
import java.math.BigInteger
import java.security.MessageDigest

internal fun RandomUserResponse.toDTO(): List<UserDTO> =
    results.toDTO()

internal fun RandomUserErrorResponse.toDTO(code: Int): ErrorDTO =
    ErrorDTO(code = 0, message = this.error.orEmpty(), cause = "")

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

// TODO: allow pagination configuration
// TODO: avoid HTTP call on next pages from last page
internal data class Page(
    val page: Int,
    val results: Int,
    val seed: String,
)