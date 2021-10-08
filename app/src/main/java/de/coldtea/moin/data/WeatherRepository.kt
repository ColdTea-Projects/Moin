package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.forecast.model.Weather
import de.coldtea.moin.domain.model.alarm.LatLong
import de.coldtea.moin.domain.model.extensions.toHourlyForecast
import de.coldtea.moin.domain.model.forecast.HourlyForecast
import de.coldtea.moin.extensions.convertToEntitylist
import de.coldtea.moin.extensions.getTopOfTheHour

class WeatherRepository(
    private val weatherForecastApi: WeatherForecastApi,
    private val moinDatabase: MoinDatabase
) {

    //TODO:check database if it duplicates
    suspend fun updateWeatherForecast(cityName: String) {
        val forecast = getWeatherForThreeDays(cityName)

        forecast?.updateForecastsDatabase(cityName)
    }

    suspend fun getHourlyForecast() = moinDatabase
        .daoHourlyForecast
        .getHourlyForecasts()
        .map {
            it.toHourlyForecast()
        }

    suspend fun getHourlyForecastByCity(cityName: String) = moinDatabase
        .daoHourlyForecast
        .getHourlyForecastsByCity(cityName)
        .map {
            it.toHourlyForecast()
        }

    private suspend fun Weather.updateForecastsDatabase(cityName: String) {
        convertToEntitylist(cityName)
            .map { hourlyForecastEntity ->
                moinDatabase.daoHourlyForecast.insert(hourlyForecastEntity)
            }

        moinDatabase.daoHourlyForecast.removeOutdatedForecasts(getTopOfTheHour())
    }

    suspend fun getForecast(): HourlyForecast? =
        moinDatabase
            .daoHourlyForecast
            .getForecastsAt(getTopOfTheHour())
            ?.toHourlyForecast()

    private suspend fun getWeatherForThreeDays(cityName: String) =
        weatherForecastApi.getForecast(cityName, 3)

    suspend fun getCurrentByLatLong(latLong: LatLong?) =
        latLong?.let { weatherForecastApi.getCurrent("${latLong.lat},${latLong.long}") }


}