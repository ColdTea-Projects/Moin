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

    var didWorksStart: Boolean
        get() = sharedPreferences.getBoolean(DID_WORKS_START, false)
        set(value) = sharedPreferences.edit().apply {
            putBoolean(DID_WORKS_START, value)
        }.apply()

    var authorizationCode: String?
        get() = sharedPreferences.getString(AUTHORIZATION_CODE,null)
        set(value) = sharedPreferences.edit().apply {
            putString(AUTHORIZATION_CODE, value)
        }.apply()

    var codeVerifier: String?
        get() = sharedPreferences.getString(CODE_VERIFIER,null)
        set(value) = sharedPreferences.edit().apply {
            putString(CODE_VERIFIER, value)
        }.apply()

    var refreshToken: String?
        get() = sharedPreferences.getString(REFRESH_TOKEN,null)
        set(value) = sharedPreferences.edit().apply {
            putString(REFRESH_TOKEN, value)
        }.apply()

    var spotifyAuthorizationBackup: String?
        get() = sharedPreferences.getString(SPOTIFY_AUTHORIZATION_BACKUP,null)
        set(value) = sharedPreferences.edit().apply {
            putString(SPOTIFY_AUTHORIZATION_BACKUP, value)
        }.apply()

    var lastVisitedCity: String?
        get() = sharedPreferences.getString(LAST_VISITED_CITY,null)
        set(value) = sharedPreferences.edit().apply {
            putString(LAST_VISITED_CITY, value)
        }.apply()

    var lastBulkForecastFetch: Int
        get() = sharedPreferences.getInt(LAST_BULK_FORECAST_FETCH, -1)
        set(value) = sharedPreferences.edit().apply(){
            putInt(LAST_BULK_FORECAST_FETCH, value)
        }.apply()

    companion object {
        private const val MOIN_APP_SHARED_PREFERENCES = "moin_app_shared_preferences"
        private const val DID_WORKS_START = "did_works_start"
        private const val AUTHORIZATION_CODE = "authorization_code"
        private const val CODE_VERIFIER = "code_verifier"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val SPOTIFY_AUTHORIZATION_BACKUP = "spotify_authorization_backup"
        private const val LAST_VISITED_CITY = "last_visited_city"
        private const val LAST_BULK_FORECAST_FETCH = "last_bulk_forecast_fetch"
    }

}