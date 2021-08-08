package de.coldtea.moin.data

import android.content.Intent
import de.coldtea.moin.data.network.spotify.SpotifyAuthApi
import de.coldtea.moin.data.network.spotify.model.TokenResponse
import de.coldtea.moin.services.AuthenticationService
import de.coldtea.moin.services.SpotifyService.CLIENT_ID
import de.coldtea.moin.services.SpotifyService.REDIRECT_URI
import de.coldtea.moin.services.model.AuthorizationResponse
import timber.log.Timber

class SpotifyAuthRepository(
    private val spotifyAuthApi: SpotifyAuthApi,
    private val authenticationService: AuthenticationService,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    fun getAuthorizationIntent(): Intent? {
        sharedPreferencesRepository.codeVerifier = authenticationService.generateCodeVerifier()

        return sharedPreferencesRepository.codeVerifier?.let {
            authenticationService.getAuthorizationIntent(it)
        }
    }

    fun registerAuthorizationCode(response: AuthorizationResponse?) {
        if (response == null) return
        if (response.error == null) Timber.i("Moin --> ${response.error}")

        if (response.state == authenticationService.state && response.code != null) {
            sharedPreferencesRepository.authorizationCode = response.code
        }
    }

    suspend fun getAccessToken(
        code: String
    ): TokenResponse? = sharedPreferencesRepository.codeVerifier?.let {
        spotifyAuthApi.getAccessToken(
            CLIENT_ID,
            GRANT_TYPE,
            code,
            REDIRECT_URI,
            it
        )
    }

    companion object {
        const val GRANT_TYPE = "authorization_code"
    }
}