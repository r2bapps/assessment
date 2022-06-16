package com.r2b.apps.lib.api.randomuser.entity

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class RandomUserResponse(
    @Json(name = "info")
    val info: Info? = Info(),
    @Json(name = "results")
    val results: List<Results>? = listOf()
)

@JsonClass(generateAdapter = true)
data class Info(
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "results")
    val results: Int? = 0,
    @Json(name = "seed")
    val seed: String? = "",
    @Json(name = "version")
    val version: String? = ""
)

@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "cell")
    val cell: String? = "",
    @Json(name = "dob")
    val dob: Dob? = Dob(),
    @Json(name = "email")
    val email: String? = "",
    @Json(name = "gender")
    val gender: String? = "",
    @Json(name = "id")
    val id: Id? = Id(),
    @Json(name = "location")
    val location: Location? = Location(),
    @Json(name = "login")
    val login: Login? = Login(),
    @Json(name = "name")
    val name: Name? = Name(),
    @Json(name = "nat")
    val nat: String? = "",
    @Json(name = "phone")
    val phone: String? = "",
    @Json(name = "picture")
    val picture: Picture? = Picture(),
    @Json(name = "registered")
    val registered: Registered? = Registered()
)

@JsonClass(generateAdapter = true)
data class Dob(
    @Json(name = "age")
    val age: Int? = 0,
    @Json(name = "date")
    val date: String? = ""
)

@JsonClass(generateAdapter = true)
data class Id(
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "value")
    val value: String? = ""
)

@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "city")
    val city: String? = "",
    @Json(name = "coordinates")
    val coordinates: Coordinates? = Coordinates(),
    @Json(name = "postcode")
    val postcode: String? = "",
    @Json(name = "state")
    val state: String? = "",
    @Json(name = "street")
    val street: Street? = Street(),
    @Json(name = "timezone")
    val timezone: Timezone? = Timezone()
)

@JsonClass(generateAdapter = true)
data class Street(
    @Json(name = "number")
    val number: String? = "",
    @Json(name = "name")
    val name: String? = "",
)

@JsonClass(generateAdapter = true)
data class Login(
    @Json(name = "md5")
    val md5: String? = "",
    @Json(name = "password")
    val password: String? = "",
    @Json(name = "salt")
    val salt: String? = "",
    @Json(name = "sha1")
    val sha1: String? = "",
    @Json(name = "sha256")
    val sha256: String? = "",
    @Json(name = "username")
    val username: String? = "",
    @Json(name = "uuid")
    val uuid: String? = ""
)

@JsonClass(generateAdapter = true)
data class Name(
    @Json(name = "first")
    val first: String? = "",
    @Json(name = "last")
    val last: String? = "",
    @Json(name = "title")
    val title: String? = ""
)

@JsonClass(generateAdapter = true)
data class Picture(
    @Json(name = "large")
    val large: String? = "",
    @Json(name = "medium")
    val medium: String? = "",
    @Json(name = "thumbnail")
    val thumbnail: String? = ""
)

@JsonClass(generateAdapter = true)
data class Registered(
    @Json(name = "age")
    val age: Int? = 0,
    @Json(name = "date")
    val date: String? = ""
)

@JsonClass(generateAdapter = true)
data class Coordinates(
    @Json(name = "latitude")
    val latitude: String? = "",
    @Json(name = "longitude")
    val longitude: String? = ""
)

@JsonClass(generateAdapter = true)
data class Timezone(
    @Json(name = "description")
    val description: String? = "",
    @Json(name = "offset")
    val offset: String? = ""
)