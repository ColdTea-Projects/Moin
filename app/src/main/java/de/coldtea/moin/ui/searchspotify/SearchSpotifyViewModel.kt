package de.coldtea.moin.ui.searchspotify

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.SongRepository
import de.coldtea.moin.data.SpotifyAuthRepository
import de.coldtea.moin.data.SpotifyRepository
import de.coldtea.moin.domain.model.alarm.AuthorizationResponse
import de.coldtea.moin.domain.model.playlist.MediaType
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.Song
import de.coldtea.moin.domain.services.SpotifyService
import de.coldtea.moin.services.model.*
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SearchSpotifyViewModel(
    private val spotifyAuthRepo: SpotifyAuthRepository,
    private val spotifyRepo: SpotifyRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val songRepository: SongRepository
) : ViewModel()
{

    var playlist: PlaylistName? = null
        set(value) {
            sharedPreferencesRepository.spotifyAuthorizationBackup?.let{ key ->
                field = PlaylistName.values().find { it.key == key }
                sharedPreferencesRepository.spotifyAuthorizationBackup = null
                return
            }

            field = value
        }

    private var _spotifyState = MutableSharedFlow<SpotifyState>()
    val spotifyState: SharedFlow<SpotifyState> = _spotifyState

    private var _spotifyAppRemote: SpotifyAppRemote? = null

    private val connectionListener = object : Connector.ConnectionListener {
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
        ConnectionParams.Builder(SpotifyService.CLIENT_ID)
            .setRedirectUri(SpotifyService.REDIRECT_URI)
            .showAuthView(true)
            .build()
    }

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

    fun search(accessToken: String, query: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val searchResponse = spotifyRepo.search(
                query,
                accessToken
            )

            _spotifyState.emit(SearchResultReceived(searchResponse))
        }catch (ex: HttpException){
            Timber.e("Moin -->  $ex")
        }catch (ex: Exception){
            Timber.e("Moin -->  $ex")
        }
    }

    fun connectSpotify(context: Context) = SpotifyAppRemote.connect(
        context,
        connectionParams,
        connectionListener
    )

    private fun subscribePlayerState() =
        _spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            viewModelScope.launch(Dispatchers.IO) {
                Timber.d("Moin --> Player state: $playerState")
                _spotifyState.emit(Play(playerState))
            }
        }

    fun disconnectSpotify() = SpotifyAppRemote.disconnect(_spotifyAppRemote)

    fun playTrack(trackId: String) =
        _spotifyAppRemote
            ?.playerApi
            ?.play("spotify:track:$trackId")
            ?.also {
                subscribePlayerState()
            }

    fun pauseTrack() =
        _spotifyAppRemote
            ?.playerApi
            ?.pause()
            ?.also {
                subscribePlayerState()
            }

    fun addSong(spotifySearchResultBundle: SpotifySearchResultBundle){
        val selectedTrack = Song(
            localId = -1,
            trackId = spotifySearchResultBundle.id,
            name = spotifySearchResultBundle.spotifySearchResultDelegateItem.songName,
            artistName = spotifySearchResultBundle.spotifySearchResultDelegateItem.artistName,
            imageUrl = spotifySearchResultBundle.spotifySearchResultDelegateItem.imageUrl.orEmpty(),
            mediaType = MediaType.SPOTIFY.ordinal,
            source = "",
            playlist = playlist?.ordinal?:-1
        )

        viewModelScope.launch(Dispatchers.IO) {
            songRepository.addSong(selectedTrack)
        }
    }

    fun backUpPlaylist(){
        sharedPreferencesRepository.spotifyAuthorizationBackup = playlist?.key
    }
}