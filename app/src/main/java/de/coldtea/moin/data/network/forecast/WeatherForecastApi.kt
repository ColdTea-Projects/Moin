package de.coldtea.moin.data.network.forecast

import de.coldtea.moin.data.network.forecast.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastApi {
    @GET("forecast.json?days=3")
    suspend fun getForecast(@Query("q") cityName: String): Weather
}