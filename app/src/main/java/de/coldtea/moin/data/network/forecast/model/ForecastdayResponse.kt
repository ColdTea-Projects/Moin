package de.coldtea.moin.data.network.forecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastdayResponse(
    @SerialName("date")
    val date: String?,
    @SerialName("date_epoch")
    val dateEpoch: Int?,
    @SerialName("hour")
    val hour: List<HourResponse>?
)