package de.coldtea.moin.data.network.forecast.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Hour(
    @Json(name = "chance_of_rain")
    val chanceOfRain: String,
    @Json(name = "chance_of_snow")
    val chanceOfSnow: String,
    val cloud: Int,
    val condition: Condition,
    @Json(name = "precip_in")
    val precipIn: Double,
    @Json(name = "precip_mm")
    val precipMm: Double,
    @Json(name = "temp_c")
    val tempC: Double,
    @Json(name = "temp_f")
    val tempF: Double,
    val time: String,
    @Json(name = "time_epoch")
    val timeEpoch: Int,
    @Json(name = "will_it_rain")
    val willItRain: Int,
    @Json(name = "will_it_snow")
    val willItSnow: Int,
    @Json(name = "wind_kph")
    val windKph: Double,
    @Json(name = "wind_mph")
    val windMph: Double,
)