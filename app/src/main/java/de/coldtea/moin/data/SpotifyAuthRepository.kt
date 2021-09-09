package de.coldtea.moin.data

import android.content.Intent
import de.coldtea.moin.data.network.spotify.SpotifyAuthApi
import de.coldtea.moin.data.network.spotify.model.TokenResponse
import de.coldtea.moin.domain.model.alarm.AuthorizationResponse
import de.coldtea.moin.domain.services.AuthenticationService
import de.coldtea.moin.domain.services.SpotifyService.CLIENT_ID
import de.coldtea.moin.domain.services.SpotifyService.REDIRECT_URI
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

    fun registerAuthorizationCode(response: AuthorizationResponse?) = when{
        response == null -> Timber.i("Moin --> AuthorizationResponse is null")
        response.error != null -> Timber.i("Moin --> ${response.error}")
        response.state == authenticationService.state && response.code != null -> sharedPreferencesRepository.authorizationCode = response.code
        else -> Timber.i("Moin --> Something went wrong! Authorization code could not be registered")
    }

    suspend fun getAccessToken(
        code: String
    ): TokenResponse? = sharedPreferencesRepository.codeVerifier?.let {
        spotifyAuthApi.getAccessToken(
            CLIENT_ID,
            GRANT_TYPE_AUTHORIZATION,
            code,
            REDIRECT_URI,
            it
        )
    }

    suspend fun getAccessTokenByRefreshToken(
        refreshToken: String
    ): TokenResponse? =
        spotifyAuthApi.getAccessTokenByRefreshToken(
            GRANT_TYPE_REFRESH,
            refreshToken,
            CLIENT_ID
        )

    companion object {
        const val GRANT_TYPE_AUTHORIZATION = "authorization_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
    }
}