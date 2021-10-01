package de.coldtea.moin.domain.model.spotify

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val scope: String,
    val expiresIn: Int,
    val refreshToken: String
)
