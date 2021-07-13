package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.forecast.model.Weather
import de.coldtea.moin.extensions.convertToEntitylist
import de.coldtea.moin.extensions.getTopOfTheHour
import de.coldtea.moin.services.model.LatLong

class WeatherRepository(
    private val weatherForecastApi: WeatherForecastApi,
    private val moinDatabase: MoinDatabase
) {
    suspend fun updateWeatherForecast(cityName: String) {
        val forecast = getWeatherForThreeDays(cityName)

        forecast?.updateForecastsDatabase(cityName)
    }

    suspend fun getHourlyForecast() = moinDatabase.daoHourlyForecast.getHourlyForecasts()

    suspend fun Weather.updateForecastsDatabase(cityName: String) {
        convertToEntitylist(cityName).map { hourlyForecastEntity ->
            moinDatabase.daoHourlyForecast.insert(hourlyForecastEntity)
        }

        moinDatabase.daoHourlyForecast.removeOutdatedForecasts(getTopOfTheHour())
    }

    suspend fun getWeatherForThreeDays(cityName: String) =
        weatherForecastApi.getForecast(cityName, 3)

    suspend fun getCurrentByLatLong(latLong: LatLong?) = latLong?.let { weatherForecastApi.getCurrent("${latLong.lat},${latLong.long}") }


}