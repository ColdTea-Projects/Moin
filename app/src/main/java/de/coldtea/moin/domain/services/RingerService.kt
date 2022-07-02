package de.coldtea.moin.domain.services

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
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
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.domain.model.playlist.MediaType
import de.coldtea.moin.domain.model.ringer.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import kotlin.math.roundToInt

class RingerService(
    private val context: Context,
    private val songRandomizeService: SongRandomizeService,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    private var mp3PlayerService: MP3PlayerService? = null
    private val ioCoroutineScope
        get() = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin.RingerService --> ioCoroutineScope crashed: $t")
        })
    private val mainCoroutineScope
        get() = CoroutineScope(Dispatchers.Main + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin.RingerService --> mainCoroutineScope crashed: $t")
        })

    private var ringerScreenInfo: RingerScreenInfo? = null
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
    var isStartedPlaying = false

    fun ring() = ioCoroutineScope.launch {
        if(isStartedPlaying) {
            ringerScreenInfo = songRandomizeService.ringerScreenInfoOfPlayingSong
            _ringerStateInfo.emit( Playing(ringerScreenInfo) )
            return@launch
        }

        ringerScreenInfo = songRandomizeService.getRingerScreenInfo()
        _ringerStateInfo.emit( Randomized(ringerScreenInfo) )
        when (ringerScreenInfo?.song?.mediaType) {
            MediaType.MP3.ordinal -> playMP3()
            null -> ringDefaultAlarm()
        }
    }

    fun stop() {
        when (ringerScreenInfo?.song?.mediaType) {
            MediaType.MP3.ordinal -> {
                mp3PlayerService?.stop()

                isStartedPlaying = false
                mainCoroutineScope.launch {
                    _ringerStateInfo.emit(Stops)
                }
            }
            null -> stopDefaultAlarm()
        }
        mp3PlayerService = null

        vibrator.cancel()
    }

    //endregion

    // region local media

    private fun playMP3(){
        if (mp3PlayerService != null) return

        mp3PlayerService = MP3PlayerService(
            context,
            FilePickerConverter.stringToUri(ringerScreenInfo?.song?.source.orEmpty())
        )

        isStartedPlaying = mp3PlayerService?.play() == true

        if (!isStartedPlaying) {
            ringDefaultAlarm()
            return
        }

        adjustVolume()
        startVibrating()

    }

    //endregion

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
    //endregion

    //region default alarm
    private fun ringDefaultAlarm() {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone?.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        ringtone?.play()
        adjustVolume()

        isStartedPlaying = true
    }

    private fun stopDefaultAlarm() {
        ringtone?.stop()
        isStartedPlaying = false
        mainCoroutineScope.launch {
            _ringerStateInfo.emit(Stops)
        }

    }
    //endregion

    //region volume adjusment

    private fun adjustVolume(){
        if(sharedPreferencesRepository.raiseVolumeGradually){
            mainCoroutineScope.launch {
                var startVolume = 0f
                while (startVolume <= sharedPreferencesRepository.volume){
                    startVolume += 0.03f
                    setAlarmVolume(startVolume)
                    delay(1000)
                }
            }
        }else{
            setAlarmVolume(sharedPreferencesRepository.volume)
        }
    }

    private fun setAlarmVolume(volume: Float){
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)

        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            (volume * maxVolume).roundToInt(),
            0
        )
    }

    //endregion
}