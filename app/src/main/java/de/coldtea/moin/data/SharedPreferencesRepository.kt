package de.coldtea.moin.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesRepository(val context: Context) {

    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            MOIN_APP_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    var didWorksStart: Boolean
        get() = sharedPreferences.getBoolean(DID_WORKS_START, false)
        set(value) = sharedPreferences.edit().apply {
            putBoolean(DID_WORKS_START, value)
        }.apply()

    companion object {
        const val MOIN_APP_SHARED_PREFERENCES = "moin_app_shared_preferences"
        const val DID_WORKS_START = "did_works_start"
    }

}