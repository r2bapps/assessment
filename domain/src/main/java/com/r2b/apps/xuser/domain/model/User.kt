package com.r2b.apps.xuser.domain.model

data class User(
    val id: Int,
    val serverId: String,
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    val picture: String,
    val gender: String,
    val location: String,
    val registeredDate: String,
    val description: String
)

