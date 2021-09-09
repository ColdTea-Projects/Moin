package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.forecast.model.Weather
import de.coldtea.moin.domain.model.alarm.LatLong
import de.coldtea.moin.extensions.convertToEntitylist
import de.coldtea.moin.extensions.getTopOfTheHour

class WeatherRepository(
    private val weatherForecastApi: WeatherForecastApi,
    private val moinDatabase: MoinDatabase
) {
    suspend fun updateWeatherForecast(cityName: String) {
        val forecast = getWeatherForThreeDays(cityName)

        forecast?.updateForecastsDatabase(cityName)
    }

    suspend fun getHourlyForecast() = moinDatabase.daoHourlyForecast.getHourlyForecasts()

    private suspend fun Weather.updateForecastsDatabase(cityName: String) {
        convertToEntitylist(cityName).map { hourlyForecastEntity ->
            moinDatabase.daoHourlyForecast.insert(hourlyForecastEntity)
        }

        moinDatabase.daoHourlyForecast.removeOutdatedForecasts(getTopOfTheHour())
    }

    private suspend fun getWeatherForThreeDays(cityName: String) =
        weatherForecastApi.getForecast(cityName, 3)

    suspend fun getCurrentByLatLong(latLong: LatLong?) = latLong?.let { weatherForecastApi.getCurrent("${latLong.lat},${latLong.long}") }


}