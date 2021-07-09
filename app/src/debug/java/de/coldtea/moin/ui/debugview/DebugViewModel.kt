package de.coldtea.moin.ui.debugview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.data.network.forecast.model.Weather
import de.coldtea.moin.services.GeolocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class DebugViewModel(
    private val weatherRepo: WeatherRepository,
    private val geolocationService: GeolocationService
): ViewModel(){

    private var _weatherResponse = MutableSharedFlow<Weather>()
    val weatherResponse: SharedFlow<Weather> = _weatherResponse

    fun getWeatherForecast(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weather = weatherRepo.getWeather(cityName)
            _weatherResponse.emit(weather)
        }
    }

    fun getCity(activity: DebugActivity) = geolocationService.getCityName(activity)
}