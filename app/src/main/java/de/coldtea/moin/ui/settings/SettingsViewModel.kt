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
        get() = (sharedPreferencesRepository.volume * MAX_VOLUME).roundToInt()
        set(value) {
            val volume = if(value < MIN_VOLUME) MIN_VOLUME else value
            sharedPreferencesRepository.volume = volume / MAX_VOLUME.toFloat()
        }

    var snoozeDuration: Int
        get() = sharedPreferencesRepository.snoozeDuration
        set(value) {
            sharedPreferencesRepository.snoozeDuration = value
        }

    companion object{
        const val DEF_SNOOZE_DURATION = 15
        const val MAX_SNOOZE_DURATION = 120
        const val MIN_SNOOZE_DURATION = 5
        const val STEP_SNOOZE_DURATION = 5
        const val MAX_VOLUME = 100
        const val MIN_VOLUME = 10
    }
}