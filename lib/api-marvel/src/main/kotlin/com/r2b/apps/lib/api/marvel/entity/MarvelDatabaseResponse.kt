package com.r2b.apps.lib.api.marvel.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarvelDatabaseResponse(
    @Json(name = "query")
    val query: MarvelDatabaseQuery? = null,
    @Json(name = "error")
    val error: MarvelDatabaseApiError? = null,
)

@JsonClass(generateAdapter = true)
data class MarvelDatabaseQuery(
    @Json(name = "pages")
    val pages: List<MarvelDatabasePage>? = null,
)

@JsonClass(generateAdapter = true)
data class MarvelDatabasePage(
    @Json(name = "pageid")
    val pageId: Long? = null,
    @Json(name = "index")
    val index: Int? = null,
    @Json(name = "title")
    val title: String? = null,
    @Json(name = "fullurl")
    val fullUrl: String? = null,
    @Json(name = "thumbnail")
    val thumbnail: MarvelDatabaseThumbnail? = null,
)

@JsonClass(generateAdapter = true)
data class MarvelDatabaseThumbnail(
    @Json(name = "source")
    val source: String? = null,
)

@JsonClass(generateAdapter = true)
data class MarvelDatabaseApiError(
    @Json(name = "code")
    val code: String? = null,
    @Json(name = "info")
    val info: String? = null,
)

@JsonClass(generateAdapter = true)
data class MarvelDatabaseApiErrorResponse(
    @Json(name = "error")
    val error: MarvelDatabaseApiError? = null,
)
