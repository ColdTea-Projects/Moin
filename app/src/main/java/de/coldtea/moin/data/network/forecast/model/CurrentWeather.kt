package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    val current: Current,
    val location: Location
)
