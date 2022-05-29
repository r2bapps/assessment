package com.r2b.apps.lib.api.marvel.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarvelErrorResponse(
    @Json(name = "code")
    val code: Int? = 0,
    @Json(name = "status")
    val status: String? = ""
)