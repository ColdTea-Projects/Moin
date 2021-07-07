package de.coldtea.moin.ui.debugview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.data.network.forecast.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class DebugViewModel(
    private val weatherRepo: WeatherRepository
): ViewModel(){

    private var _weatherResponse = MutableSharedFlow<Weather>()
    val weatherResponse: SharedFlow<Weather> = _weatherResponse

    fun getWeatherForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            val weather = weatherRepo.getWeather()
            _weatherResponse.emit(weather)
        }
    }
}