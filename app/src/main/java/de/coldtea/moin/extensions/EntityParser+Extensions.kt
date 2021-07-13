package de.coldtea.moin.extensions

import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.model.Weather

fun Weather.convertToEntitylist(cityName: String): List<HourlyForecastEntity> {
    val hourlyForecastEntityList = mutableListOf<HourlyForecastEntity>()
    forecast.forecastday.map { day ->
        day.hour.map { hour ->
            val hourlyForecastEntity = HourlyForecastEntity(
                hourlyForecastId = hour.timeEpoch,
                city = cityName,
                date = day.date,
                time = hour.time,
                timeEpoch = hour.timeEpoch,
                tempC = hour.tempC,
                tempF = hour.tempF,
                conditionCode = hour.condition.code,
                conditionText = hour.condition.text
            )

            hourlyForecastEntityList.add(hourlyForecastEntity)
        }
    }

    return hourlyForecastEntityList.toList()
}