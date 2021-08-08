package de.coldtea.moin.data.network.spotify.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessTokenRequestParameters (
    @Json(name = "client_id")
    val clientId: String,
    @Json(name = "grant_type")
    val grantType: String,
    @Json(name = "code")
    val code: String,
    @Json(name = "redirect_uri")
    val redirectUri: String,
    @Json(name = "code_verifier")
    val codeVerifier: String
)