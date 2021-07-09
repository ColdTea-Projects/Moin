package de.coldtea.moin.data

import de.coldtea.moin.data.network.forecast.WeatherForecastApi

class WeatherRepository (private val weatherForecastApi: WeatherForecastApi) {
    suspend fun getWeather(cityName: String) = weatherForecastApi.getForecast(cityName)
}