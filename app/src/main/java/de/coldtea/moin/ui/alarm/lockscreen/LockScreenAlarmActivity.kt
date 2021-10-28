package de.coldtea.moin.ui.alarm.lockscreen

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.ActivityLockScreenAlarmBinding
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo
import de.coldtea.moin.extensions.activateLockScreen
import de.coldtea.moin.extensions.deactivateLockScreen
import de.coldtea.moin.services.model.ConnectionFailed
import de.coldtea.moin.services.model.ConnectionSuccess
import de.coldtea.moin.services.model.Play
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LockScreenAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockScreenAlarmBinding
    private val viewModel: LockScreenAlarmViewModel by viewModel()
    private var ringtone: Ringtone? = null
    private var ringerScreenInfo: RingerScreenInfo? = null
    private var isStartedPlaying = false

    private val vibrator: Vibrator by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        binding = ActivityLockScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStateObserver()
        observeRingerItems()
        observeSpotifyState()
        observeSavedAlarmInfo()

    }

    override fun onStart() {
        super.onStart()
        setupUIItems()
        viewModel.requestAlarmForUI()
        activateLockScreen()

        viewModel.ring()
    }

    override fun onDestroy() {
        super.onDestroy()

        vibrator.cancel()
        viewModel.disconnectSpotify()
        viewModel.dismissNotification(this)

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

    private fun initStateObserver() = lifecycleScope.launch {
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

    private fun observeRingerItems() = lifecycleScope.launch {
        viewModel.ringerStateInfo.collect {
            ringerScreenInfo = it
            viewModel.connectSpotify(this@LockScreenAlarmActivity)
        }
    }

    private fun observeSpotifyState() = lifecycleScope.launch {
        viewModel.spotifyState.collect {
            when (it) {
                ConnectionSuccess -> onConnectionSuccess()
                ConnectionFailed -> onConnectionFailed()
                is Play -> {
                    if (it.playerState.isPaused && isStartedPlaying) finish()
                    else {
                        isStartedPlaying = true
                    }
                }
            }
        }
    }

    private fun observeSavedAlarmInfo() = lifecycleScope.launch {
        viewModel.label.collect {
            binding.label.text = it
        }
    }

    private fun onConnectionSuccess() {
        val songId = ringerScreenInfo?.songId

        startVibrating()

        if (songId != null) viewModel.playTrack(songId)
        else ringDefaultAlarm()
    }

    private fun onConnectionFailed() {
        ringDefaultAlarm()
    }

    private fun ringDefaultAlarm() {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        ringtone?.streamType = AudioManager.STREAM_ALARM
        ringtone?.play()
    }

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

    companion object {
        private val alarmPattern
            get() = arrayListOf<Long>().apply {
                for(i in 0..300){
                    add(1000)
                }
            }.toLongArray()
    }

}