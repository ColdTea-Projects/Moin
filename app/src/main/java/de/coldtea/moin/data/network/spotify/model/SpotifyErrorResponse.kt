package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyErrorResponse(
    @SerialName("error")
    val error: String?,
    @SerialName("error_description")
    val errorDescription: String?
){
    fun isInvalidGrant() = !error.isNullOrEmpty() && error.equals(ERROR_INVALID_GRANT)

    companion object{
        private const val ERROR_INVALID_GRANT = "invalid_grant"
    }
}