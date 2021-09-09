package de.coldtea.moin.data.network.spotify.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Album(
    @Json(name = "album_type")
    val albumType: String?,
    @Json(name = "artists")
    val artists: List<Artist?>?,
    @Json(name = "available_markets")
    val availableMarkets: List<String?>?,
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls?,
    @Json(name = "href")
    val href: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "images")
    val images: List<Image?>?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "release_date_precision")
    val releaseDatePrecision: String?,
    @Json(name = "total_tracks")
    val totalTracks: Int?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "uri")
    val uri: String?
)