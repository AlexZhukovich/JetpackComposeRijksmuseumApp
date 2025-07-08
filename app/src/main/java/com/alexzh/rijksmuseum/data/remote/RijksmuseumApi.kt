package com.alexzh.rijksmuseum.data.remote

import com.alexzh.rijksmuseum.data.remote.model.ArtObjectDetailsResponse
import com.alexzh.rijksmuseum.data.remote.model.ArtObjectsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RijksmuseumApi {
    @GET("collection")
    suspend fun getArtObjects(
        @Query("p") page: Int,
        @Query("ps") pageSize: Int
    ): ArtObjectsResponse

    @GET("collection/{objectNumber}")
    suspend fun getArtObjectDetails(
        @Path("objectNumber") objectNumber: String
    ): ArtObjectDetailsResponse
}