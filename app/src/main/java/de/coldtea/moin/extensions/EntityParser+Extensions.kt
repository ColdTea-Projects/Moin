package de.coldtea.moin.extensions

import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.model.WeatherResponse

fun WeatherResponse.convertToEntitylist(cityName: String): List<HourlyForecastEntity> {
    val hourlyForecastEntityList = mutableListOf<HourlyForecastEntity>()
    forecastResponse?.forecastdayResponse?.map { day ->
        day.hour?.map { hour ->
            val hourlyForecastEntity = HourlyForecastEntity(
                hourlyForecastId = hour.timeEpoch ?: -1,
                city = cityName,
                date = day.date.orEmpty(),
                time = hour.time.orEmpty(),
                timeEpoch = hour.timeEpoch ?: -1,
                tempC = hour.tempC,
                tempF = hour.tempF,
                conditionCode = hour.conditionResponse?.code ?: -1,
                conditionText = hour.conditionResponse?.text.orEmpty()
            )

            hourlyForecastEntityList.add(hourlyForecastEntity)
        }
    }

    return hourlyForecastEntityList.toList()
}