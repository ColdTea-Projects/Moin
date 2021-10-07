package de.coldtea.moin.domain.model.ringer

data class RingerScreenInfo (
    val forecastText: String,
    val songId: String?,
    val tempC: Double,
    val tempF: Double,
)