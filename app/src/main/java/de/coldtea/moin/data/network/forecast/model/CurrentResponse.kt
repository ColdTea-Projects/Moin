package de.coldtea.moin.data.network.forecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentResponse(
    @SerialName("cloud")
    val cloud: Int?,
    @SerialName("condition")
    val conditionResponse: ConditionResponse?,
    @SerialName("precip_in")
    val precipIn: Double?,
    @SerialName("precip_mm")
    val precipMm: Double?,
    @SerialName("temp_c")
    val tempC: Double?,
    @SerialName("temp_f")
    val tempF: Double?,
    @SerialName("wind_kph")
    val windKph: Double?,
    @SerialName("wind_mph")
    val windMph: Double?
)