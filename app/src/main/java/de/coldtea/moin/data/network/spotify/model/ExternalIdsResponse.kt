package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalIdsResponse(
    @SerialName("isrc")
    val isrc: String?
)