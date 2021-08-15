package de.coldtea.moin.data.network.spotify

import de.coldtea.moin.data.network.spotify.model.TokenResponse
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotifyAuthApi {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("api/token")
    suspend fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("grant_type") grantType: String,
        @Query("code") code: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("code_verifier") codeVerifier: String
    ): TokenResponse?

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("api/token")
    suspend fun getAccessTokenByRefreshToken(
        @Query("grant_type") grantType: String,
        @Query("refresh_token") code: String,
        @Query("client_id") clientId: String
    ): TokenResponse?

}