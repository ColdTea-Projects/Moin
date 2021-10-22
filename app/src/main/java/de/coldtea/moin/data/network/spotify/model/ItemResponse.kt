package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(
    @SerialName("album")
    val albumResponse: AlbumResponse?,
    @SerialName("artists")
    val artistResponse: List<ArtistResponse?>?,
    @SerialName("available_markets")
    val availableMarkets: List<String?>?,
    @SerialName("disc_number")
    val discNumber: Int?,
    @SerialName("duration_ms")
    val durationMs: Int?,
    @SerialName("explicit")
    val explicit: Boolean?,
    @SerialName("external_ids")
    val externalIdResponse: ExternalIdsResponse?,
    @SerialName("external_urls")
    val externalUrlResponse: ExternalUrlsResponse?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("is_local")
    val isLocal: Boolean?,
    @SerialName("name")
    val name: String?,
    @SerialName("popularity")
    val popularity: Int?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("track_number")
    val trackNumber: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)