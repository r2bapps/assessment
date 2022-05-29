package com.r2b.apps.xuser.data

import com.r2b.apps.xuser.data.api.entity.UserDTO
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.User

val FAKE_USER_LIST: List<User> = listOf(
    User(0, "1488597362", "Ricky", "Olson", "ricky.olson@example.com", "02-7385-9798", "https://randomuser.me/api/portraits/men/78.jpg", "male", "Blossom Hill Rd, 1619", "2009-09-20T00:26:23.824Z", "description"),
)

val FAKE_USER_DTO_LIST: List<UserDTO> = listOf(
    UserDTO("1488597362", "Ricky", "Olson", "ricky.olson@example.com", "02-7385-9798", "https://randomuser.me/api/portraits/men/78.jpg", "male", "Blossom Hill Rd, 1619", "2009-09-20T00:26:23.824Z", "description"),
)

val FAKE_USER_DB_LIST: List<UserDB> = listOf(
    UserDB(0, "1488597362", "Ricky", "Olson", "ricky.olson@example.com", "02-7385-9798", "https://randomuser.me/api/portraits/men/78.jpg", "male", "Blossom Hill Rd, 1619", "2009-09-20T00:26:23.824Z", "description", false),
)
