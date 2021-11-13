package de.coldtea.moin.ui.settings

import androidx.lifecycle.ViewModel
import de.coldtea.moin.data.SharedPreferencesRepository
import kotlin.math.roundToInt

class SettingsViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository
): ViewModel() {

    var raiseVolumeGradually
        get() = sharedPreferencesRepository.raiseVolumeGradually
        set(value) {
            sharedPreferencesRepository.raiseVolumeGradually = value
        }

    var volume: Int
        get() = (sharedPreferencesRepository.volume * 100).roundToInt()
        set(value) {
            val volume = if(value < 10) 10 else value
            sharedPreferencesRepository.volume = volume / 100f
        }
}