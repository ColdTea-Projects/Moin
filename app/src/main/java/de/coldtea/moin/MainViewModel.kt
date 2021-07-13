package de.coldtea.moin

import androidx.lifecycle.ViewModel
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.services.GeolocationService

class MainViewModel(
    val sharedPreferencesRepository: SharedPreferencesRepository,
    val geolocationService: GeolocationService
): ViewModel()