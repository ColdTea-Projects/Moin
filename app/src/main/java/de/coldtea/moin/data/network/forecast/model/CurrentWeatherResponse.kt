package de.coldtea.moin.data.network.forecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherResponse(
    @SerialName("current")
    val currentResponse: CurrentResponse?,
    @SerialName("location")
    val locationResponse: LocationResponse?
)
