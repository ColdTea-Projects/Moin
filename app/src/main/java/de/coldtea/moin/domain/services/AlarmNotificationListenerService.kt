package de.coldtea.moin.domain.services

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.PlayerApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import de.coldtea.moin.domain.model.alarm.NotificationListenerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class AlarmNotificationListenerService: NotificationListenerService() {
    private var notificationId = -1
    private val smplrAlarmService: SmplrAlarmService by KoinJavaComponent.inject(SmplrAlarmService::class.java)
    private val songRandomizeService: SongRandomizeService by KoinJavaComponent.inject(SongRandomizeService::class.java)
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO)
    private var ringtone: Ringtone? = null

    override fun onListenerConnected() {
        Timber.i("Moin --> onListenerConnected")
        super.onListenerConnected()
        Timber.i("Moin --> onListenerConnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Timber.i("Moin --> onNotificationPosted")
        super.onNotificationPosted(sbn)
        Timber.i("Moin --> onNotificationPosted")
        notificationId = sbn?.id?:-1
        val keyguardManager = applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


        //is screen not locked
        if (!keyguardManager.isKeyguardLocked){
            observeAlarmList()
            smplrAlarmService.callRequestAlarmList()
            sbn?.id?.let { ring() }
            SpotifyAppRemote.connect(
                applicationContext,
                connectionParams,
                connectionListener
            )
        }
    }

    //TODO: Write a service for spotify to prevent duplicated code
//    private var _spotifyState = MutableSharedFlow<SpotifyState>()
//    val spotifyState: SharedFlow<SpotifyState> = _spotifyState

    private var _spotifyAppRemote: SpotifyAppRemote? = null

    private val connectionListener = object : Connector.ConnectionListener {
        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
            _spotifyAppRemote = spotifyAppRemote
            Timber.d("Moin --> Spotify Connected!")

            ring()
        }

        override fun onFailure(error: Throwable?) {
            Timber.d("Moin --> $error")
            ringDefaultAlarm()
        }
    }

    private val connectionParams by lazy {
        ConnectionParams.Builder(SpotifyService.CLIENT_ID)
            .setRedirectUri(SpotifyService.REDIRECT_URI)
            .showAuthView(true)
            .build()
    }

    private fun observeAlarmList() = ioCoroutineScope.launch {
        smplrAlarmService.alarmList.collect { alarmObject ->

            if (alarmObject.alarmEvent is NotificationListenerRequest) {
                alarmObject.alarmList.alarmItems.find { it.requestId == notificationId  }?.let {
                    SpotifyAppRemote.connect(
                        applicationContext,
                        connectionParams,
                        connectionListener
                    )
                }
            }
        }
    }

    private fun ring() = ioCoroutineScope.launch{
        val ringerScreenInfo = songRandomizeService.getRingerScreenInfo()

        _spotifyAppRemote
            ?.playerApi
            ?.play("spotify:track:${ringerScreenInfo?.song}", PlayerApi.StreamType.ALARM)
            ?.also {
                subscribePlayerState()
            }
    }

    private fun subscribePlayerState() =
        _spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            onPlayerStateChanged(playerState)
        }

    private fun ringDefaultAlarm(){
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        ringtone?.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        ringtone?.play()
    }

    private fun onPlayerStateChanged(playerState: PlayerState){
        Timber.i(playerState.toString())
    }
}