package de.coldtea.moin.ui.debugview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.model.CurrentWeather
import de.coldtea.moin.services.GeolocationService
import de.coldtea.moin.services.model.LatLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class DebugViewModel(
    private val weatherRepo: WeatherRepository,
    private val geolocationService: GeolocationService
) : ViewModel() {

    private var _weatherResponse = MutableSharedFlow<List<HourlyForecastEntity>>()
    val weatherResponse: SharedFlow<List<HourlyForecastEntity>> = _weatherResponse

    private var _currentResponse = MutableSharedFlow<CurrentWeather>()
    val currentResponse: SharedFlow<CurrentWeather> = _currentResponse

    fun getWeatherForecast(cityName: String, latLong: LatLong?) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val current = weatherRepo.getCurrentByLatLong(latLong)
                if (current != null) _currentResponse.emit(current)
            } catch (ex: HttpException) {
                Timber.e("Moin-getCurrentByLatLong- HTTP Request Error: $ex")
            } catch (ex: Exception) {
                Timber.e("Moin-getCurrentByLatLong- Error: $ex")
            }

            try {
                val weather = weatherRepo.getHourlyForecast()
                _weatherResponse.emit(weather)
            } catch (ex: HttpException) {
                Timber.e("Moin-getWeatherForecast- HTTP Request Error: $ex")
            } catch (ex: Exception) {
                Timber.e("Moin-getWeatherForecast- Error: $ex")
            }
        }
    }

    fun getCity() = geolocationService.getCityName()
    fun getLocation() = geolocationService.getLatLong()
}