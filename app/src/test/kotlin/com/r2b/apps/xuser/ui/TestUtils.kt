package com.r2b.apps.xuser.ui

import com.r2b.apps.xuser.domain.model.User

val FAKE_USER_LIST: List<User> = listOf(
    User(0, "0", "Capitan America", "surname", "email", "phone", "picture", "gender", "location", "registeredDate", "description"),
    User(1, "1", "Hulk", "surname", "email", "phone", "picture", "gender", "location", "registeredDate", "description"),
    User(2, "2", "Spiderman", "surname", "email", "phone", "picture", "gender", "location", "registeredDate", "description"),
)

class FakesBuilder {
    fun user(id: Int): User = User(
        id,
        serverId = "",
        name = "",
        surname = "",
        email = "",
        phone = "",
        picture = "",
        gender = "",
        location = "",
        registeredDate = "",
        description = "",
    )
}