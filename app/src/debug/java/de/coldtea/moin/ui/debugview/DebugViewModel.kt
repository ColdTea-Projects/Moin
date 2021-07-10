package de.coldtea.moin.ui.debugview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.model.CurrentWeather
import de.coldtea.moin.services.GeolocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class DebugViewModel(
    private val weatherRepo: WeatherRepository,
    private val geolocationService: GeolocationService
): ViewModel(){

    private var _weatherResponse = MutableSharedFlow<List<HourlyForecastEntity>>()
    val weatherResponse: SharedFlow<List<HourlyForecastEntity>> = _weatherResponse

    private var _currentResponse = MutableSharedFlow<CurrentWeather>()
    val currentResponse: SharedFlow<CurrentWeather> = _currentResponse

    fun getWeatherForecast(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val current = weatherRepo.getCurrent(cityName)
            _currentResponse.emit(current)

            val weather = weatherRepo.getWeatherForecast(cityName)
            _weatherResponse.emit(weather)
        }
    }

    fun getCity(activity: DebugActivity) = geolocationService.getCityName(activity)
}