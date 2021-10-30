package de.coldtea.moin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.domain.services.GeolocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

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
        val currentCity = geolocationService.getCityName()//TODO: why the hell this is null now?

        if (!currentCity.isNullOrEmpty() && currentCity != sharedPreferencesRepository.lastVisitedCity) {
            Timber.i("Moin --> updateWeatherForecast - main view model")
            weatherRepository.updateWeatherForecast(currentCity)
            sharedPreferencesRepository.lastVisitedCity = currentCity
        }
    }


}