package com.r2b.apps.xuser.data.filter

import com.r2b.apps.xuser.domain.model.User
import javax.inject.Inject

class UserFilterDelegate @Inject constructor() {

    fun filterBy(users: List<User>, text: String) =
        users.filter {
            filterByName(it, text) ||
            filterBySurname(it, text) ||
            filterByEmail(it, text)
        }

    private fun filterByName(user: User, text: String): Boolean =
        user.name.startsWith(text, ignoreCase = true)

    private fun filterBySurname(user: User, text: String): Boolean =
        user.surname.startsWith(text, ignoreCase = true)

    private fun filterByEmail(user: User, text: String): Boolean =
        user.email.startsWith(text, ignoreCase = true)

}