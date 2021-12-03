package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.forecast.model.WeatherResponse
import de.coldtea.moin.domain.model.alarm.LatLong
import de.coldtea.moin.domain.model.extensions.toForecastBoundary
import de.coldtea.moin.domain.model.extensions.toHourlyForecast
import de.coldtea.moin.domain.model.forecast.HourlyForecast
import de.coldtea.moin.extensions.convertToEntitylist
import de.coldtea.moin.extensions.getTopOfTheHour
import retrofit2.HttpException
import timber.log.Timber

class WeatherRepository(
    private val weatherForecastApi: WeatherForecastApi,
    private val moinDatabase: MoinDatabase
) {

    //TODO:check database if it duplicates
    suspend fun updateWeatherForecast(cityName: String) {
        val forecast = getWeatherForThreeDays(cityName)

        forecast?.updateForecastsDatabase(cityName)
    }

    suspend fun getHourlyForecast() =
        try {
            moinDatabase
                .daoHourlyForecast
                .getHourlyForecasts()
                .map {
                    it.toHourlyForecast()
                }
        } catch (e: HttpException) {
            Timber.e("Moin --> getHourlyForecast: $e")
            null
        }

    suspend fun getHourlyForecastByCity(cityName: String) =
        try {
            moinDatabase
                .daoHourlyForecast
                .getHourlyForecastsByCity(cityName)
                .map {
                    it.toHourlyForecast()
                }
        } catch (e: HttpException) {
            Timber.e("Moin --> getHourlyForecastByCity: $e")
            null
        }

    private suspend fun WeatherResponse.updateForecastsDatabase(cityName: String) {
        convertToEntitylist(cityName)
            .map { hourlyForecastEntity ->
                moinDatabase.daoHourlyForecast.insert(hourlyForecastEntity)
            }

        moinDatabase.daoHourlyForecast.removeOutdatedForecasts(getTopOfTheHour())
    }

    suspend fun getForecast(): HourlyForecast? =
        try {
            moinDatabase
                .daoHourlyForecast
                .getForecastsAt(getTopOfTheHour())
                ?.toHourlyForecast()
        } catch (e: HttpException) {
            Timber.e("Moin --> getForecast: $e")
            null
        }

    private suspend fun getWeatherForThreeDays(cityName: String): WeatherResponse? =
        try {
            weatherForecastApi.getForecast(cityName, 3)
        } catch (e: HttpException) {
            Timber.e("Moin --> getWeatherForThreeDays: $e")
            null
        }

    suspend fun getCurrentByLatLong(latLong: LatLong?) =
        latLong?.let { weatherForecastApi.getCurrent("${latLong.lat},${latLong.long}") }

    suspend fun isUpdateNeeded(cityName: String): Boolean {
        val hourlyForecastByCity = getHourlyForecastByCity(cityName)?:return false
        val forecastBoundaryObject = hourlyForecastByCity.toForecastBoundary()

        if (forecastBoundaryObject.noForecastData()
            || forecastBoundaryObject.dataSizeNotBigEnough()
            || forecastBoundaryObject.isOutdated()
        ) return true

        return false
    }
}