package com.r2b.apps.lib.api.marvel.retrofit

import com.r2b.apps.lib.api.NetworkResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelCharacterResponse
import com.r2b.apps.lib.api.marvel.entity.MarvelErrorResponse
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelService {

    companion object {

        const val baseUrl = "https://gateway.marvel.com/v1/public/"

        object Headers {
            val common: Map<String, String> = mapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/json; charset=UTF-8",
            )
        }

        const val defaultLimit = 20
        const val defaultOffset = 0

    }

    @GET("characters")
    suspend fun characters(
        @HeaderMap headers: Map<String, String>? = Headers.common,
        @Query("limit") limit: Int? = defaultLimit,
        @Query("offset") offset: Int? = defaultOffset,
    ): NetworkResponse<MarvelCharacterResponse, MarvelErrorResponse>

    @GET("characters/{characterId}")
    suspend fun characterById(
        @HeaderMap headers: Map<String, String> = Headers.common,
        @Path("characterId") characterId: String
    ): NetworkResponse<MarvelCharacterResponse, MarvelErrorResponse>

}