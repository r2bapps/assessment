package com.r2b.apps.xuser.data

import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.User


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
