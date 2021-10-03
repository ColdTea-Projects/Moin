package de.coldtea.moin.data.network.spotify.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalUrlsResponse(
    @Json(name = "spotify")
    val spotify: String?
)