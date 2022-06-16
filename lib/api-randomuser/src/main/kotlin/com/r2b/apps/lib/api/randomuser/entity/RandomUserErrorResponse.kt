package com.r2b.apps.lib.api.randomuser.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RandomUserErrorResponse(
    @Json(name = "error")
    val error: String? = ""
)