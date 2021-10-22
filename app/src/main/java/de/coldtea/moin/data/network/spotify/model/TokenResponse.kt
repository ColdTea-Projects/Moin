package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String?,
    @SerialName("token_type")
    val tokenType: String?,
    @SerialName("expires_in")
    val expiresIn: Int?,
    @SerialName("refresh_token")
    val refreshToken: String?
)
