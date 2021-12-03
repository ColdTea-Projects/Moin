package de.coldtea.moin.ui.lockscreen

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ActivityLockScreenAlarmBinding
import de.coldtea.moin.domain.model.alarm.AlarmItem
import de.coldtea.moin.domain.model.forecast.CLOUDY_WEATHER_BUNDLE
import de.coldtea.moin.domain.model.forecast.RAINY_WEATHER_BUNDLE
import de.coldtea.moin.domain.model.forecast.SNOWY_WEATHER_BUNDLE
import de.coldtea.moin.domain.model.forecast.SUNNY_WEATHER_BUNDLE
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo
import de.coldtea.moin.extensions.activateLockScreen
import de.coldtea.moin.extensions.deactivateLockScreen
import de.coldtea.moin.extensions.getTimeText
import de.coldtea.moin.extensions.isDayTime
import de.coldtea.moin.ui.alarm.lockscreen.models.AlarmItemReceived
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.RingerScreenInfoReceived
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import de.coldtea.moin.ui.lockscreen.models.MotionLayoutAction
import de.coldtea.moin.ui.lockscreen.models.OnDismissDrag
import de.coldtea.moin.ui.lockscreen.models.OnSnoozeDrag
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class LockScreenAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockScreenAlarmBinding
    private val viewModel: LockScreenAlarmViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityLockScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStateObserver()
    }

    override fun onStart() {
        super.onStart()
        setupUIItems()
        viewModel.requestAlarmForUI()
        activateLockScreen()

    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.onScreenDestroyed(this)
        deactivateLockScreen()
    }

    private fun setupUIItems() = with(binding) {
        snoozeMotionLayout.setTransitionListener(LockScreenTransitionListener(OnSnoozeDrag))
        dismissMotionLayout.setTransitionListener(LockScreenTransitionListener(OnDismissDrag))
    }

    private fun initStateObserver() = lifecycleScope.launch {
        viewModel.lockScreenState.collect { state ->
            Timber.i("Moin --> $state")
            when (state) {
                is Ringing -> {
                    viewModel.requestId =
                        intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID, -1)
                    viewModel.ring()
                }
                is AlarmItemReceived -> binding.projectAlarmItem(alarmItem = state.alarmItem)
                is RingerScreenInfoReceived -> binding.projectRingerScreenInfo(ringerScreenInfo = state.ringerScreenInfo)
                is Done -> {
                    finish()
                }
            }
        }
    }

    private fun ActivityLockScreenAlarmBinding.projectAlarmItem(alarmItem: AlarmItem) {
        time.text = with(alarmItem) { hour to minute }.getTimeText()
        label.text = alarmItem.info.label
    }

    private fun ActivityLockScreenAlarmBinding.projectRingerScreenInfo(ringerScreenInfo: RingerScreenInfo) {
        setAnimation(ringerScreenInfo.forecastCode)
        forecast.text = getString(
            R.string.weather_forecast,
            ringerScreenInfo.forecastText,
            ringerScreenInfo.tempC.toString(),
            ringerScreenInfo.tempF.toString()
        )
        ringerScreenInfo.song?.let { playingSong ->
            song.text = getString(R.string.song_playing, playingSong.name)
        }
    }

    private fun ActivityLockScreenAlarmBinding.setAnimation(forecastCode: Int) {
        val animation = when {

            SUNNY_WEATHER_BUNDLE.conditionCodes.contains(forecastCode) -> {
                if(!Calendar.getInstance().isDayTime()) R.raw.sunny
                else R.raw.clear
            }
            CLOUDY_WEATHER_BUNDLE.conditionCodes.contains(forecastCode) -> R.raw.cloudy
            RAINY_WEATHER_BUNDLE.conditionCodes.contains(forecastCode) -> R.raw.rainy
            SNOWY_WEATHER_BUNDLE.conditionCodes.contains(forecastCode) -> R.raw.snow

            else -> R.raw.default_anim
        }

        with(weather){
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            setAnimation(animation)
            playAnimation()
        }
    }

    inner class LockScreenTransitionListener(private val motionLayoutAction: MotionLayoutAction) :
        MotionLayout.TransitionListener {
        var progress = 0F
        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

        }

        override fun onTransitionChange(
            motionLayout: MotionLayout?,
            startId: Int,
            endId: Int,
            progress: Float
        ) {
            this.progress = progress
        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            if (progress > TRANSITION_ACTIVATION_THRESHOLD) {
                when (motionLayoutAction) {
                    OnDismissDrag -> {
                        if (viewModel.isRinging) {
                            viewModel.dismissAlarm()
                        } else {
                            finish()
                        }
                    }
                    OnSnoozeDrag -> {
                        if (viewModel.isRinging) {
                            viewModel.snoozeAlarm()
                        } else {
                            finish()
                        }
                    }
                }
            }
        }

        override fun onTransitionTrigger(
            motionLayout: MotionLayout?,
            triggerId: Int,
            positive: Boolean,
            progress: Float
        ) {
        }

    }

    companion object {
        private const val TRANSITION_ACTIVATION_THRESHOLD = 0.5F
    }

}