package de.coldtea.moin.data.network.forecast

import de.coldtea.moin.data.network.forecast.model.CurrentWeatherResponse
import de.coldtea.moin.data.network.forecast.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastApi {
    @GET("forecast.json")
    suspend fun getForecast(@Query("q") cityName: String, @Query("days") days: Int): WeatherResponse?

    @GET("current.json")
    suspend fun getCurrent(@Query("q") cityName: String): CurrentWeatherResponse?
}