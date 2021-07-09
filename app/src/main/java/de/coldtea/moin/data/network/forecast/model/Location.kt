package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    val country: String,
    val lat: Double,
    val localtime: String,
    @Json(name = "localtime_epoch")
    val localtimeEpoch: Int,
    val lon: Double,
    val name: String,
    val region: String,
    @Json(name = "tz_id")
    val tzId: String
)