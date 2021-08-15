package de.coldtea.moin.data.network.spotify.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistX(
    @Json(name = "external_urls")
    val externalUrls: ExternalUrlsXX?,
    @Json(name = "href")
    val href: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "uri")
    val uri: String?
)