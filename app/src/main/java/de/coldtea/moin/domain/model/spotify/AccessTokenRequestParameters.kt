package de.coldtea.moin.domain.model.spotify

data class AccessTokenRequestParameters (
    val clientId: String,
    val grantType: String,
    val code: String,
    val redirectUri: String,
    val codeVerifier: String
)