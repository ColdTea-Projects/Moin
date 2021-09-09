package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Forecastday(
    val date: String?,
    @Json(name = "date_epoch")
    val dateEpoch: Int?,
    val hour: List<Hour>?
)