package de.coldtea.moin.domain.services

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.PlayerApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.coldtea.moin.domain.model.playlist.MediaType
import de.coldtea.moin.domain.model.ringer.Randomized
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo
import de.coldtea.moin.domain.model.ringer.RingerStateInfo
import de.coldtea.moin.domain.model.ringer.Stops
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

class RingerService(
    private val context: Context,
    private val songRandomizeService: SongRandomizeService
) {
    private val ioCoroutineScope
        get() = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin --> ioCoroutineScope crashed: $t")
        })
    private val mainCoroutineScope
        get() = CoroutineScope(Dispatchers.Main + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin --> mainCoroutineScope crashed: $t")
        })

    private var ringerScreenInfo: RingerScreenInfo? = null
    private var isStartedPlaying = false
    private val vibrator: Vibrator
        get() = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val alarmPattern
        get() = arrayListOf<Long>().apply {
            for (i in 0..300) {
                add(1000)
            }
        }.toLongArray()
    private var ringtone: Ringtone? = null

    //region ringer
    private val _ringerStateInfo = MutableSharedFlow<RingerStateInfo?>()
    val ringerStateInfo = _ringerStateInfo.asSharedFlow()

    fun ring() = ioCoroutineScope.launch {
        ringerScreenInfo = songRandomizeService.getRingerScreenInfo()
        _ringerStateInfo.emit(
            Randomized(ringerScreenInfo)
        )
        when (ringerScreenInfo?.song?.mediaType) {
            MediaType.SPOTIFY.ordinal -> {
                withContext(mainCoroutineScope.coroutineContext){
                    connectSpotify()
                }
            }
            null -> ringDefaultAlarm()
        }
    }

    fun stop() {
        when (ringerScreenInfo?.song?.mediaType) {
            MediaType.SPOTIFY.ordinal -> pauseTrack()
            null -> stopDefaultAlarm()
        }
        isStartedPlaying = false

        vibrator.cancel()
    }

    fun invalidate() {
        disconnectSpotify()
    }

    //end region

    //region spotify
//    private var _spotifyState = MutableSharedFlow<SpotifyState>()
//    val spotifyState: SharedFlow<SpotifyState> = _spotifyState

    private var _spotifyAppRemote: SpotifyAppRemote? = null

    private val connectionListener = object : Connector.ConnectionListener {
        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
            _spotifyAppRemote = spotifyAppRemote
            Timber.d("Moin --> Spotify Connected!")
            ioCoroutineScope.launch {
                onConnectionSuccess()
            }
        }

        override fun onFailure(error: Throwable?) {
            ioCoroutineScope.launch {
                Timber.d("Moin --> $error")
                onConnectionFailed()
            }
        }
    }

    private fun onConnectionSuccess() {
        val songId = ringerScreenInfo?.song?.trackId
        startVibrating()

        if (songId != null) playTrack(songId)
        else ringDefaultAlarm()
    }

    private fun onConnectionFailed() {
        ringDefaultAlarm()
    }

    private val connectionParams by lazy {
        ConnectionParams.Builder(SpotifyService.CLIENT_ID)
            .setRedirectUri(SpotifyService.REDIRECT_URI)
            .showAuthView(true)
            .build()
    }

    private fun connectSpotify() = SpotifyAppRemote.connect(
        context,
        connectionParams,
        connectionListener
    )

    private fun subscribePlayerState() =
        _spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            mainCoroutineScope.launch {
                Timber.d("Moin --> Player state: $playerState")
                if (playerState.isPaused && isStartedPlaying) {
                    isStartedPlaying = false
                    _ringerStateInfo.emit(Stops)
                }
                else {
                    isStartedPlaying = true
                }
            }
        }

    private fun disconnectSpotify() = SpotifyAppRemote.disconnect(_spotifyAppRemote)

    private fun playTrack(songId: String) =
        _spotifyAppRemote
            ?.playerApi
            ?.play("spotify:track:${songId}", PlayerApi.StreamType.ALARM)
            ?.also {
                subscribePlayerState()
            }

    private fun pauseTrack() =
        _spotifyAppRemote
            ?.playerApi
            ?.pause()
            ?.also {
                subscribePlayerState()
            }

    //end region

    //region vibration

    private fun startVibrating() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                alarmPattern,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        vibrator.vibrate(10000)
    }
    //end region

    //Region default alarm
    private fun ringDefaultAlarm() {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone?.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        ringtone?.play()
    }

    private fun stopDefaultAlarm(){
        ringtone?.stop()
        mainCoroutineScope.launch {
            _ringerStateInfo.emit(Stops)
        }

    }
    //end region
}