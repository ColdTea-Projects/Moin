package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TempData(
    val temp: Double,
    val humidity: Int
)