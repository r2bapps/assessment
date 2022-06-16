package com.r2b.apps.xuser.data

import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.api.entity.ErrorDTO
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.DomainError
import com.r2b.apps.xuser.domain.model.User
import java.io.IOException

fun IOException.toErrorDTO(): ErrorDTO =
    ErrorDTO(code = 0, message = "No Internet connection", cause = this.message.orEmpty())

fun Throwable.toErrorDTO(): ErrorDTO =
    ErrorDTO(code = 0, message = "Unknown error", cause = this.message.orEmpty())

// Starting with Android 9 (API level 28), cleartext support is disabled by default.
fun switchToHttps(url: String): String =
    if (url.contains("https")) url else url.replace("http", "https")

fun String?.toIntOrDefault(default: Int = 0) =
    this?.toInt() ?: default

fun ErrorDTO.toDomainError(): DomainError =
    DomainError(
        code = this.code ?: 0,
        message = message.orEmpty(),
        cause = cause.orEmpty(),
    )

fun List<UserDTO>.dtoToDBEntity(): List<UserDB> =
    this.map { it.dtoToDBEntity() }

fun UserDTO.dtoToDBEntity(): UserDB =
    UserDB(
        serverId = serverId.orEmpty(),
        name = name.orEmpty(),
        surname = surname.orEmpty(),
        email = email.orEmpty(),
        phone = phone.orEmpty(),
        picture = picture.orEmpty(),
        gender = gender.orEmpty(),
        location = location.orEmpty(),
        registeredDate = registeredDate.orEmpty(),
        description = description.orEmpty(),
    )

fun List<UserDB>.dbEntityToDomain(): List<User> =
    this.map { it.dbEntityToDomain() }

fun UserDB.dbEntityToDomain(): User =
    User(
        id = id!!,
        serverId = serverId.orEmpty(),
        name = name.orEmpty(),
        surname = surname.orEmpty(),
        email = email.orEmpty(),
        phone = phone.orEmpty(),
        picture = picture.orEmpty(),
        gender = gender.orEmpty(),
        location = location.orEmpty(),
        registeredDate = registeredDate.orEmpty(),
        description = description.orEmpty(),
    )

fun List<User>.domainToDBEntity(): List<UserDB> =
    this.map { it.domainToDBEntity() }

fun User.domainToDBEntity(): UserDB =
    UserDB(
        id = id,
        serverId = serverId,
        name = name,
        surname = surname,
        email = email,
        phone = phone,
        picture = picture,
        gender = gender,
        location = location,
        registeredDate = registeredDate,
        description = description,
    )
