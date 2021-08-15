package de.coldtea.moin.data.network.spotify

import de.coldtea.moin.data.network.spotify.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface SpotifyApi {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("v1/search")
    suspend fun search(
        @Query("q") q: String,
        @Query("type") type: String,
        @Header("Authorization") auth: String
    ): SearchResponse?

}