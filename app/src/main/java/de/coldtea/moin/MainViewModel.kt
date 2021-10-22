package de.coldtea.moin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.domain.services.GeolocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val geolocationService: GeolocationService,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    val locationServicesPermited = geolocationService.permitted
    val didWorksStart = sharedPreferencesRepository.didWorksStart

    fun requestLocationServicePermissions(activity: MainActivity) =
        geolocationService.requestLocationPermit(activity)

    fun saveLocation() {
        sharedPreferencesRepository.lastVisitedCity = geolocationService.getCityName()
    }

    fun updateForecastIfLocationChanged() = viewModelScope.launch(Dispatchers.IO) {
        val currentCity = geolocationService.getCityName()

        if (currentCity != null && currentCity != sharedPreferencesRepository.lastVisitedCity) {
            weatherRepository.updateWeatherForecast(currentCity)
            sharedPreferencesRepository.lastVisitedCity = currentCity
        }
    }


}