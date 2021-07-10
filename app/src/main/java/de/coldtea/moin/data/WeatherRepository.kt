package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.extensions.convertToEntitylist

class WeatherRepository(
    private val weatherForecastApi: WeatherForecastApi,
    private val moinDatabase: MoinDatabase
) {
    suspend fun getWeatherForecast(cityName: String): List<HourlyForecastEntity> {
        val forecast = getWeatherForThreeDays(cityName)

        forecast.convertToEntitylist().map { hourlyForecastEntity ->
            moinDatabase.daoHourlyForecast.insert(hourlyForecastEntity)
        }

        return moinDatabase.daoHourlyForecast.getHourlyForecasts()
    }

    suspend fun getWeatherForThreeDays(cityName: String) =
        weatherForecastApi.getForecast(cityName, 3)

    suspend fun getCurrent(cityName: String) = weatherForecastApi.getCurrent(cityName)

}