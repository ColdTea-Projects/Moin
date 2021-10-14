
package de.coldtea.moin.ui.alarm.lockscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.PlayerApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.coldtea.moin.domain.model.alarm.*
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.domain.services.SongRandomizeService
import de.coldtea.moin.domain.services.SpotifyService
import de.coldtea.moin.services.model.ConnectionFailed
import de.coldtea.moin.services.model.ConnectionSuccess
import de.coldtea.moin.services.model.Play
import de.coldtea.moin.services.model.SpotifyState
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.LockScreenState
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.util.*

class LockScreenAlarmViewModel(
    private val songRandomizeService: SongRandomizeService
) : ViewModel() {

    private val smplrAlarmService: SmplrAlarmService by inject(SmplrAlarmService::class.java)
    var requestId = -1

    private val _lockScreenState = MutableStateFlow<LockScreenState>(Ringing)
    val lockScreenState: StateFlow<LockScreenState> = _lockScreenState

    private val _label = MutableSharedFlow<String>()
    val label: SharedFlow<String> = _label

    private val _ringerStateInfo = MutableSharedFlow<RingerScreenInfo?>()
    val ringerStateInfo: SharedFlow<RingerScreenInfo?> = _ringerStateInfo

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.alarmList.collect { alarmObject ->

                when (alarmObject.alarmEvent) {
                    SnoozeAlarmUpdate,
                    DismissAlarmUpdate -> _lockScreenState.emit(Done)
                    DismissAlarmRequest -> alarmObject.alarmList.alarmItems
                        .find { alarmItem -> alarmItem.requestId == requestId }
                        ?.let { alarmItem ->
                            setAlarmForDismissal(alarmItem)
                        }
                    RequestAlarms -> _label.emit(alarmObject.alarmList.alarmItems[0].info?.label.orEmpty())
                }
            }


        }
    }

    fun ring() = viewModelScope.launch {
        _ringerStateInfo.emit(songRandomizeService.getRingerScreenInfo())
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

    fun playTrack(songId: String) =
        _spotifyAppRemote
            ?.playerApi
            ?.play("spotify:track:${songId}", PlayerApi.StreamType.ALARM)
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

    fun dismissAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.callRequestAlarmList(DismissAlarmRequest)
        }
        pauseTrack()
    }

    fun requestAlarmForUI() = smplrAlarmService.callRequestAlarmList(
        alarmEvent = RequestAlarms
    )

    private fun setAlarmForDismissal(alarmItem: AlarmItem) {
        smplrAlarmService.updateAlarm(
            requestId = requestId,
            hour = alarmItem.info?.originalHour?.toIntOrNull(),
            minute = alarmItem.info?.originalMinute?.toIntOrNull(),
            isActive = false,
            alarmEvent = DismissAlarmUpdate
        )
    }

    fun snoozeAlarm() =
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.updateAlarm(
                requestId = requestId,
                hour = snoozeTime.first,
                minute = snoozeTime.second,
                isActive = true,
                alarmEvent = SnoozeAlarmUpdate
            )
        }.also {
            pauseTrack()
        }

    private val snoozeTime: Pair<Int, Int>
        get() = Calendar.getInstance().let {
            it.add(Calendar.MINUTE, 15)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }
}