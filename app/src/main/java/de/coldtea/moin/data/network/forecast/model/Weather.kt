package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "main") val temp: TempData,
    @Json(name = "name") val name: String
)