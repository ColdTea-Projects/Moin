package de.coldtea.moin.data.network.spotify.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalIds(
    @Json(name = "isrc")
    val isrc: String?
)