package de.coldtea.moin.ui.debugview

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.SpotifyAuthRepository
import de.coldtea.moin.data.SpotifyRepository
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.data.network.forecast.model.CurrentWeather
import de.coldtea.moin.domain.model.alarm.AuthorizationResponse
import de.coldtea.moin.domain.model.alarm.LatLong
import de.coldtea.moin.domain.model.forecast.HourlyForecast
import de.coldtea.moin.domain.services.GeolocationService
import de.coldtea.moin.domain.services.SpotifyService.CLIENT_ID
import de.coldtea.moin.domain.services.SpotifyService.REDIRECT_URI
import de.coldtea.moin.services.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class DebugViewModel(
    private val weatherRepo: WeatherRepository,
    private val spotifyAuthRepo: SpotifyAuthRepository,
    private val spotifyRepo: SpotifyRepository,
    private val geolocationService: GeolocationService,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    private var _spotifyAppRemote: SpotifyAppRemote? = null

    private var _weatherResponse = MutableSharedFlow<List<HourlyForecast>>()
    val weatherResponse: SharedFlow<List<HourlyForecast>> = _weatherResponse

    private var _currentResponse = MutableSharedFlow<CurrentWeather>()
    val currentResponse: SharedFlow<CurrentWeather> = _currentResponse

    private var _spotifyState = MutableSharedFlow<SpotifyState>()
    val spotifyState: SharedFlow<SpotifyState> = _spotifyState

    val refreshTokenExist: Boolean
        get() = sharedPreferencesRepository.refreshToken != null

    val authorizationCode: String
        get() = sharedPreferencesRepository.authorizationCode.orEmpty()

    private val connectionListener = object : ConnectionListener {
        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
            _spotifyAppRemote = spotifyAppRemote
            Timber.d("Moin --> Spotify Connected!")
            viewModelScope.launch(Dispatchers.IO) {
                _spotifyState.emit(ConnectionSuccess)
            }
        }

        override fun onFailure(error: Throwable?) {
            viewModelScope.launch(Dispatchers.IO) {
                Timber.d("Moin --> $error")
                _spotifyState.emit(ConnectionFailed)
            }
        }
    }

    private val connectionParams by lazy {
        ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
    }

    fun getWeatherForecast(latLong: LatLong?) =
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val current = weatherRepo.getCurrentByLatLong(latLong)
                if (current != null) _currentResponse.emit(current)
            } catch (ex: HttpException) {
                Timber.e("Moin-getCurrentByLatLong- HTTP Request Error: $ex")
            } catch (ex: Exception) {
                Timber.e("Moin-getCurrentByLatLong- Error: $ex")
            }

            try {
                val weather = weatherRepo.getHourlyForecast()
                _weatherResponse.emit(weather)
            } catch (ex: HttpException) {
                Timber.e("Moin-getWeatherForecast- HTTP Request Error: $ex")
            } catch (ex: Exception) {
                Timber.e("Moin-getWeatherForecast- Error: $ex")
            }
        }


    fun getCity() = geolocationService.getCityName()
    fun getLocation() = geolocationService.getLatLong()
    fun getAuthorizationIntent(): Intent? = spotifyAuthRepo.getAuthorizationIntent()

    fun connectSpotify(context: Context) = SpotifyAppRemote.connect(
        context,
        connectionParams,
        connectionListener
    )

    fun disconnectSpotify() = SpotifyAppRemote.disconnect(_spotifyAppRemote)

    fun playPlaylist() =
        _spotifyAppRemote?.playerApi?.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
            .also { subscribePlayerState() }

    private fun subscribePlayerState() =
        _spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            viewModelScope.launch(Dispatchers.IO) {
                Timber.d("Moin --> Player state: $playerState")
                _spotifyState.emit(Play(playerState))
            }
        }

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

    fun search(accessToken: String, query: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val searchResult = spotifyRepo.search(
                query,
                accessToken
            )

            _spotifyState.emit(SearchResultReceived(searchResult))
        }catch (ex: HttpException){
            Timber.e("Moin -->  $ex")
        }catch (ex: Exception){
            Timber.e("Moin -->  $ex")
        }
    }

}