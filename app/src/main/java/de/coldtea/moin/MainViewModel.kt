package de.coldtea.moin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.domain.services.GeolocationService
import de.coldtea.moin.domain.workmanager.ForecastUpdateWorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val geolocationService: GeolocationService,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    val locationServicesPermited = geolocationService.permitted

    fun requestLocationServicePermissions(activity: MainActivity) =
        geolocationService.requestLocationPermit(activity)

    fun saveLocation() {
        sharedPreferencesRepository.lastVisitedCity = geolocationService.getCityName()
    }

    fun updateForecastIfNeeded() = viewModelScope.launch(Dispatchers.IO) {
        val currentCity = geolocationService.getCityName()?:return@launch

        if (weatherRepository.isUpdateNeeded(currentCity)) {
            Timber.i("Moin --> updateWeatherForecast - main view model")
            weatherRepository.updateWeatherForecast(currentCity)
            sharedPreferencesRepository.lastVisitedCity = currentCity
        }
    }

    fun startUpdateWork(applicationContext: Context) = viewModelScope.launch(Dispatchers.IO){
        saveLocation()
        startWeatherForecastPeriodicalUpdateWork(applicationContext)
    }

    private fun startWeatherForecastPeriodicalUpdateWork(applicationContext: Context) {
        ForecastUpdateWorkManager.startPeriodicalForecastUpdate(applicationContext)
    }


}