package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequestParametersResponse (
    @SerialName("client_id")
    val clientId: String?,
    @SerialName("grant_type")
    val grantType: String?,
    @SerialName("code")
    val code: String?,
    @SerialName("redirect_uri")
    val redirectUri: String?,
    @SerialName("code_verifier")
    val codeVerifier: String?
)