package de.coldtea.moin.data.network.spotify.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(
    @Json(name = "access_token")
    val accessToken: String?,
    @Json(name = "token_type")
    val tokenType: String?,
    @Json(name = "scope")
    val scope: String?,
    @Json(name = "expires_in")
    val expiresIn: Int?,
    @Json(name = "refresh_token")
    val refreshToken: String?
)
