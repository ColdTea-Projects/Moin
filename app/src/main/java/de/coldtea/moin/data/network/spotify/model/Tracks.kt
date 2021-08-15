package de.coldtea.moin.data.network.spotify.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tracks(
    @Json(name = "href")
    val href: String?,
    @Json(name = "items")
    val items: List<Item?>?,
    @Json(name = "limit")
    val limit: Int?,
    @Json(name = "next")
    val next: String?,
    @Json(name = "offset")
    val offset: Int?,
    @Json(name = "previous")
    val previous: Any?,
    @Json(name = "total")
    val total: Int?
)