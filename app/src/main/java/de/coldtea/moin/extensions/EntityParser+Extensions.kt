package de.coldtea.moin.extensions

import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.model.Weather

fun Weather.convertToEntitylist(cityName: String): List<HourlyForecastEntity> {
    val hourlyForecastEntityList = mutableListOf<HourlyForecastEntity>()
    forecast?.forecastday?.map { day ->
        day.hour?.map { hour ->
            val hourlyForecastEntity = HourlyForecastEntity(
                hourlyForecastId = hour.timeEpoch ?: -1,
                city = cityName,
                date = day.date.orEmpty(),
                time = hour.time.orEmpty(),
                timeEpoch = hour.timeEpoch ?: -1,
                tempC = hour.tempC,
                tempF = hour.tempF,
                conditionCode = hour.condition?.code ?: -1,
                conditionText = hour.condition?.text.orEmpty()
            )

            hourlyForecastEntityList.add(hourlyForecastEntity)
        }
    }

    return hourlyForecastEntityList.toList()
}