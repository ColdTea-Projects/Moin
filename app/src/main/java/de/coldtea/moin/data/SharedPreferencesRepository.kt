package de.coldtea.moin.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesRepository(val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            MOIN_APP_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    var lastVisitedCity: String?
        get() = sharedPreferences.getString(LAST_VISITED_CITY,null)
        set(value) = sharedPreferences.edit().apply {
            putString(LAST_VISITED_CITY, value)
        }.apply()

    var volume: Float
        get() = sharedPreferences.getFloat(VOLUME, 1.0f)
        set(value) = sharedPreferences.edit().apply {
            putFloat(VOLUME, value)
        }.apply()

    var raiseVolumeGradually: Boolean
        get() = sharedPreferences.getBoolean(RAISE_VOLUME_GRADUALLY, false)
        set(value) = sharedPreferences.edit().apply {
            putBoolean(RAISE_VOLUME_GRADUALLY, value)
        }.apply()

    var snoozeDuration: Int
        get() = sharedPreferences.getInt(SNOOZE_DURATION, 15)
        set(value) = sharedPreferences.edit().apply {
            putInt(SNOOZE_DURATION, value)
        }.apply()

    companion object {
        private const val MOIN_APP_SHARED_PREFERENCES = "moin_app_shared_preferences"
        private const val LAST_VISITED_CITY = "last_visited_city"
        private const val VOLUME = "volume"
        private const val RAISE_VOLUME_GRADUALLY = "raise_volume_gradually"
        private const val SNOOZE_DURATION = "snooze_duration"

    }

}