package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TracksResponse(
    @SerialName("href")
    val href: String?,
    @SerialName("items")
    val items: List<ItemResponse?>?,
    @SerialName("limit")
    val limit: Int?,
    @SerialName("next")
    val next: String?,
    @SerialName("offset")
    val offset: Int?,
    @Contextual
    @SerialName("previous")
    val previous: Any?,
    @SerialName("total")
    val total: Int?
)