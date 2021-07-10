package de.coldtea.moin.data.network.forecast

import de.coldtea.moin.data.network.forecast.model.CurrentWeather
import de.coldtea.moin.data.network.forecast.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastApi {
    @GET("forecast.json")
    suspend fun getForecast(@Query("q") cityName: String, @Query("days") days: Int): Weather

    @GET("current.json")
    suspend fun getCurrent(@Query("q") cityName: String): CurrentWeather
}