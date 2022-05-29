package com.r2b.apps.lib.api.randomuser.retrofit

import com.r2b.apps.lib.api.randomuser.entity.RandomUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserService {

    companion object {
        const val baseUrl = "https://randomuser.me/api/"

        const val jsonFormat: String = "json"
        const val jsonPrettyFormat: String = "pretty"
        const val csvFormat: String = "csv"
        const val yamlFormat: String = "yaml"
        const val xmlFormat: String = "xml"

        const val firstPage = 1
        const val defaultResults = 20
        const val defaultSeed = "seed"
    }

    // TODO add all api options: inc, exc, ...
    @GET("./")
    suspend fun users(
        @Query("format") format: String? = jsonFormat,
        @Query("page") page: Int? = firstPage,
        @Query("results") results: Int? = defaultResults,
        @Query("seed") seed: String? = defaultSeed,
    ): RandomUserResponse

}
