package com.r2b.apps.lib.api.marvel.entity

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class MarvelCharacterResponse(
    @Json(name = "attributionHTML")
    val attributionHTML: String? = "",
    @Json(name = "attributionText")
    val attributionText: String? = "",
    @Json(name = "code")
    val code: String? = "",
    @Json(name = "copyright")
    val copyright: String? = "",
    @Json(name = "data")
    val data: Data? = Data(),
    @Json(name = "etag")
    val etag: String? = "",
    @Json(name = "status")
    val status: String? = ""
)

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "count")
    val count: String? = "",
    @Json(name = "limit")
    val limit: String? = "",
    @Json(name = "offset")
    val offset: String? = "",
    @Json(name = "results")
    val results: List<Result>? = listOf(),
    @Json(name = "total")
    val total: String? = ""
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "comics")
    val comics: Comics? = Comics(),
    @Json(name = "description")
    val description: String? = "",
    @Json(name = "events")
    val events: Events? = Events(),
    @Json(name = "id")
    val id: String? = "",
    @Json(name = "modified")
    val modified: String? = "",
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "resourceURI")
    val resourceURI: String? = "",
    @Json(name = "series")
    val series: Series? = Series(),
    @Json(name = "stories")
    val stories: Stories? = Stories(),
    @Json(name = "thumbnail")
    val thumbnail: Thumbnail? = Thumbnail(),
    @Json(name = "urls")
    val urls: List<Url>? = listOf()
)

@JsonClass(generateAdapter = true)
data class Comics(
    @Json(name = "available")
    val available: String? = "",
    @Json(name = "collectionURI")
    val collectionURI: String? = "",
    @Json(name = "items")
    val items: List<Item>? = listOf(),
    @Json(name = "returned")
    val returned: String? = ""
)

@JsonClass(generateAdapter = true)
data class Events(
    @Json(name = "available")
    val available: String? = "",
    @Json(name = "collectionURI")
    val collectionURI: String? = "",
    @Json(name = "items")
    val items: List<ItemX>? = listOf(),
    @Json(name = "returned")
    val returned: String? = ""
)

@JsonClass(generateAdapter = true)
data class Series(
    @Json(name = "available")
    val available: String? = "",
    @Json(name = "collectionURI")
    val collectionURI: String? = "",
    @Json(name = "items")
    val items: List<ItemXX>? = listOf(),
    @Json(name = "returned")
    val returned: String? = ""
)

@JsonClass(generateAdapter = true)
data class Stories(
    @Json(name = "available")
    val available: String? = "",
    @Json(name = "collectionURI")
    val collectionURI: String? = "",
    @Json(name = "items")
    val items: List<ItemXXX>? = listOf(),
    @Json(name = "returned")
    val returned: String? = ""
)

@JsonClass(generateAdapter = true)
data class Thumbnail(
    @Json(name = "extension")
    val extension: String? = "",
    @Json(name = "path")
    val path: String? = ""
)

@JsonClass(generateAdapter = true)
data class Url(
    @Json(name = "type")
    val type: String? = "",
    @Json(name = "url")
    val url: String? = ""
)

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "resourceURI")
    val resourceURI: String? = ""
)

@JsonClass(generateAdapter = true)
data class ItemX(
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "resourceURI")
    val resourceURI: String? = ""
)

@JsonClass(generateAdapter = true)
data class ItemXX(
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "resourceURI")
    val resourceURI: String? = ""
)

@JsonClass(generateAdapter = true)
data class ItemXXX(
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "resourceURI")
    val resourceURI: String? = "",
    @Json(name = "type")
    val type: String? = ""
)