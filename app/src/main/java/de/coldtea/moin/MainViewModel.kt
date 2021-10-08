package de.coldtea.moin

import androidx.lifecycle.ViewModel
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.domain.services.GeolocationService

class MainViewModel(
    val sharedPreferencesRepository: SharedPreferencesRepository,
    val geolocationService: GeolocationService
): ViewModel(){
    init {
        sharedPreferencesRepository.lastVisitedCity = geolocationService.getCityName()
    }
}