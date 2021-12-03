package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.domain.model.forecast.ForecastBoundary
import de.coldtea.moin.domain.model.forecast.HourlyForecast
import de.coldtea.moin.domain.model.playlist.Song
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo

fun HourlyForecast.toRingerScreenInfo(song: Song?) =
    RingerScreenInfo(
        forecastCode = conditionCode,
        forecastText = conditionText,
        song = song,
        tempC = tempC,
        tempF = tempF
    )

fun List<HourlyForecast>.toForecastBoundary() =
    ForecastBoundary(
        latestForecastTimeEpoch = this.lastOrNull()?.timeEpoch?:-1,
        earliestForecastTimeEpoch = this.firstOrNull()?.timeEpoch?:-1,
        dataSize = this.size
    )

fun HourlyForecastEntity.toHourlyForecast() =
    HourlyForecast(
        tempC = tempC?:-91.0,
        tempF = tempF?:-131.0,
        conditionCode = conditionCode,
        conditionText = conditionText,
        timeEpoch = timeEpoch
    )