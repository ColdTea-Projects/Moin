package de.coldtea.moin.domain.model.forecast

data class HourlyForecast(
    val tempC: Double,
    val tempF: Double,
    val conditionCode: Int,
    val conditionText: String
)