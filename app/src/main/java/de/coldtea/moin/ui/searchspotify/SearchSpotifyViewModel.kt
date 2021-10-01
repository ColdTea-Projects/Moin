package de.coldtea.moin.ui.searchspotify

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.SpotifyAuthRepository
import de.coldtea.moin.data.SpotifyRepository
import de.coldtea.moin.domain.model.alarm.AuthorizationResponse
import de.coldtea.moin.services.model.AccessTokenReceived
import de.coldtea.moin.services.model.SpotifyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SearchSpotifyViewModel(
    private val spotifyAuthRepo: SpotifyAuthRepository,
    private val spotifyRepo: SpotifyRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel()
{

    private var _spotifyState = MutableSharedFlow<SpotifyState>()
    val spotifyState: SharedFlow<SpotifyState> = _spotifyState

    val refreshTokenExist: Boolean
        get() = sharedPreferencesRepository.refreshToken != null

    fun getAuthorizationIntent(): Intent? = spotifyAuthRepo.getAuthorizationIntent()

    fun registerAuthorizationCode(response: AuthorizationResponse?) =
        spotifyAuthRepo.registerAuthorizationCode(response)

    fun getAccessToken(code: String?) = viewModelScope.launch(Dispatchers.IO) {
        if (code == null) return@launch
        try {
            val tokenResponse = spotifyAuthRepo.getAccessToken(code)
            sharedPreferencesRepository.refreshToken = tokenResponse?.refreshToken

            _spotifyState.emit(AccessTokenReceived(tokenResponse))
        }catch (ex: HttpException){
            Timber.e("Moin -->  $ex")
        }catch (ex: Exception){
            Timber.e("Moin -->  $ex")
        }

    }

    fun getAccessTokenByRefreshToken() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val tokenResponse = sharedPreferencesRepository.refreshToken?.let {
                spotifyAuthRepo.getAccessTokenByRefreshToken(
                    it
                )
            }
            sharedPreferencesRepository.refreshToken = tokenResponse?.refreshToken

            _spotifyState.emit(AccessTokenReceived(tokenResponse))
        }catch (ex: HttpException){
            Timber.e("Moin -->  $ex")
        }catch (ex: Exception){
            Timber.e("Moin -->  $ex")
        }

    }
}