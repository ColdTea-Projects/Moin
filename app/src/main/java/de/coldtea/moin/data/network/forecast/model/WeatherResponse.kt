package de.coldtea.moin.data.network.forecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("forecast")
    val forecastResponse: ForecastResponse?,
    @SerialName("location")
    val locationResponse: LocationResponse?
)









