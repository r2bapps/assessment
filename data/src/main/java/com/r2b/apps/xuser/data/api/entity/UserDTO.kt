package com.r2b.apps.xuser.data.api.entity

data class UserDTO(
    val serverId: String,
    val name: String,
    val surname: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val picture: String,
    val gender: String? = "",
    val location: String? = "",
    val registeredDate: String? = "",
    val description: String? = "",
)
