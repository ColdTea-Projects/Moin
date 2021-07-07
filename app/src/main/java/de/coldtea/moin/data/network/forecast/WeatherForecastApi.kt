package de.coldtea.moin.data.network.forecast

import de.coldtea.moin.data.network.forecast.model.Weather
import retrofit2.http.GET

interface WeatherForecastApi {
    @GET("weather?q=Helsinki&units=metric")
    suspend fun getForecast(): Weather
}