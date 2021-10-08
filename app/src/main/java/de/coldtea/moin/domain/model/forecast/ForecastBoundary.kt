package de.coldtea.moin.domain.model.forecast

import de.coldtea.moin.extensions.getTopOfTheHour

data class ForecastBoundary(
    private val latestForecastTimeEpoch: Int,
    private val earliestForecastTimeEpoch: Int,
    private val dataSize: Int
){
    fun noForecastData() = dataSize == 0
    fun dataSizeNotBigEnough() = dataSize < (latestForecastTimeEpoch - earliestForecastTimeEpoch + HOUR_IN_SECONDS) / HOUR_IN_SECONDS
    fun isOutdated() = latestForecastTimeEpoch - HOUR_IN_SECONDS <= getTopOfTheHour()

    companion object{
        private const val HOUR_IN_SECONDS = 3600
    }
}