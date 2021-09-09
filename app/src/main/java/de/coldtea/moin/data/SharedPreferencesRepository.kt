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

    companion object {
        private const val MOIN_APP_SHARED_PREFERENCES = "moin_app_shared_preferences"
        private const val DID_WORKS_START = "did_works_start"
        private const val AUTHORIZATION_CODE = "authorization_code"
        private const val CODE_VERIFIER = "code_verifier"
        private const val REFRESH_TOKEN = "refresh_token"
    }

}