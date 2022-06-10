package com.r2b.apps.xuser.ui

import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.list.adapter.model.UserListItem

val FAKE_USER_LIST: List<User> = listOf(
    User(0, "0", "Capitan America", "surname", "email", "phone", "picture", "gender", "location", "registeredDate", "description"),
    User(1, "1", "Hulk", "surname", "email", "phone", "picture", "gender", "location", "registeredDate", "description"),
    User(2, "2", "Spiderman", "surname", "email", "phone", "picture", "gender", "location", "registeredDate", "description"),
)

val FAKE_USER_LIST_ITEM: List<UserListItem> = FAKE_USER_LIST.map { UserListItem(it) }

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