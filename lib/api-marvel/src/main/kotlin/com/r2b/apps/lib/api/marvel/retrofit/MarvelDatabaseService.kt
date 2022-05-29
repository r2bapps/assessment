package com.r2b.apps.lib.api.marvel.retrofit

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseApiErrorResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelDatabaseResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MarvelDatabaseService {

    companion object {
        const val baseUrl = "https://marvel.fandom.com/"
        const val defaultLimit = 20
        const val defaultOffset = 0
    }

    @Headers(
        "Accept: application/json",
        "User-Agent: XUserAndroid/1.0 (Marvel Database client)",
    )
    @GET("api.php")
    suspend fun characters(
        @Query("action") action: String = "query",
        @Query("generator") generator: String = "search",
        @Query("gsrsearch") search: String = "incategory:Characters",
        @Query("gsrnamespace") namespace: Int = 0,
        @Query("gsrlimit") limit: Int = defaultLimit,
        @Query("gsroffset") offset: Int = defaultOffset,
        @Query("prop") properties: String = "pageimages|info",
        @Query("piprop") imageProperties: String = "thumbnail",
        @Query("pithumbsize") imageWidth: Int = 300,
        @Query("inprop") pageProperties: String = "url",
        @Query("format") format: String = "json",
        @Query("formatversion") formatVersion: Int = 2,
    ): NetworkResponse<MarvelDatabaseResponse, MarvelDatabaseApiErrorResponse>
}
