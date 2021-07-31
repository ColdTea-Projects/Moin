package de.coldtea.moin.ui.debugview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.network.forecast.model.CurrentWeather
import de.coldtea.moin.services.GeolocationService
import de.coldtea.moin.services.SpotifyService.CLIENT_ID
import de.coldtea.moin.services.SpotifyService.REDIRECT_URI
import de.coldtea.moin.services.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class DebugViewModel(
    private val weatherRepo: WeatherRepository,
    private val geolocationService: GeolocationService
) : ViewModel() {

    private var _spotifyAppRemote: SpotifyAppRemote? = null

    private var _weatherResponse = MutableSharedFlow<List<HourlyForecastEntity>>()
    val weatherResponse: SharedFlow<List<HourlyForecastEntity>> = _weatherResponse

    private var _currentResponse = MutableSharedFlow<CurrentWeather>()
    val currentResponse: SharedFlow<CurrentWeather> = _currentResponse

    private var _spotifyState = MutableSharedFlow<SpotifyState>()
    val spotifyState: SharedFlow<SpotifyState> = _spotifyState

    private val connectionListener = object: ConnectionListener {
        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?){
            _spotifyAppRemote = spotifyAppRemote
            Timber.d("Moin --> Spotify Connected!")
            viewModelScope.launch(Dispatchers.IO) {
                _spotifyState.emit(ConnectionSuccess)
            }
        }

        override fun onFailure(error: Throwable?){
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

    fun getWeatherForecast(cityName: String, latLong: LatLong?) {
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
    }

    fun getCity() = geolocationService.getCityName()
    fun getLocation() = geolocationService.getLatLong()

    fun connectSpotify(context: Context) = SpotifyAppRemote.connect(
        context,
        connectionParams,
        connectionListener
    )

    fun disconnectSpotify() = SpotifyAppRemote.disconnect(_spotifyAppRemote)

    fun playPlaylist() = _spotifyAppRemote?.playerApi?.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL").also { subscribePlayerState() }

    private fun subscribePlayerState() = _spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Moin --> Player state: $playerState")
            _spotifyState.emit(Play(playerState))
        }
    }
}