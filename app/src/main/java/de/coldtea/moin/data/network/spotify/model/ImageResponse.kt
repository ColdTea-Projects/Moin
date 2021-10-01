package de.coldtea.moin.data.network.spotify.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageResponse(
    @Json(name = "height")
    val height: Int?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "width")
    val width: Int?
)