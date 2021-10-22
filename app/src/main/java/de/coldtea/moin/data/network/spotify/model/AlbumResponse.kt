package de.coldtea.moin.data.network.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumResponse(
    @SerialName("album_type")
    val albumType: String?,
    @SerialName("artists")
    val artists: List<ArtistResponse?>?,
    @SerialName("available_markets")
    val availableMarkets: List<String?>?,
    @SerialName("external_urls")
    val externalUrls: ExternalUrlsResponse?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("images")
    val images: List<ImageResponse?>?,
    @SerialName("name")
    val name: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("release_date_precision")
    val releaseDatePrecision: String?,
    @SerialName("total_tracks")
    val totalTracks: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)