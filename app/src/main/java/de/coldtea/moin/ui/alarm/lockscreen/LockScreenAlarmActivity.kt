package de.coldtea.moin.ui.alarm.lockscreen

import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.ActivityLockScreenAlarmBinding
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo
import de.coldtea.moin.extensions.activateLockScreen
import de.coldtea.moin.extensions.deactivateLockScreen
import de.coldtea.moin.services.model.ConnectionFailed
import de.coldtea.moin.services.model.ConnectionSuccess
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LockScreenAlarmActivity : AppCompatActivity() {
    //TODO: When the alarm rings, check if the city is the current city, if not get current
    //if it is the same: check is there any record in DB, if not get current
    // if there is any record -> Use it and avoid new API request

    private lateinit var binding: ActivityLockScreenAlarmBinding
    private val viewModel: LockScreenAlarmViewModel by viewModel()
    private var ringtone: Ringtone? = null
    private var ringerScreenInfo: RingerScreenInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            initStateObserver()
            observeRingerItems()
            observeSpotifyState()
        }

    }

    override fun onStart() {
        super.onStart()
        setupUIItems()
        activateLockScreen()

        viewModel.ring()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSpotify()
        deactivateLockScreen()
    }

    private fun setupUIItems() = with(binding) {
        dismiss.setOnClickListener {
            viewModel.dismissAlarm()
        }

        snooze.setOnClickListener {
            viewModel.snoozeAlarm()
        }
    }

    private suspend fun initStateObserver() = lifecycleScope.launch {
        viewModel.lockScreenState.collect { state ->
            when (state) {
                is Ringing -> {
                    viewModel.requestId =
                        intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID, -1)
                }
                is Done -> {
                    ringtone?.stop()
                    finish()
                }
            }
        }
    }

    private suspend fun observeRingerItems() = lifecycleScope.launch {
        viewModel.ringerStateInfo.collect {
            ringerScreenInfo = it
            viewModel.connectSpotify(this@LockScreenAlarmActivity)
        }
    }

    private suspend fun observeSpotifyState() = lifecycleScope.launch {
        viewModel.spotifyState.collect {
            when (it) {
                ConnectionSuccess -> onConnectionSuccess()
                ConnectionFailed -> onConnectionFailed()
            }
        }
    }

    private fun onConnectionSuccess() {
        val songId = ringerScreenInfo?.songId

        if (songId != null) viewModel.playTrack(songId)
        else ringDefaultAlarm()
    }

    private fun onConnectionFailed() {
        ringDefaultAlarm()
    }

    private fun ringDefaultAlarm(){
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        ringtone?.streamType = AudioManager.STREAM_ALARM
        ringtone?.play()
    }

}