package de.coldtea.moin.ui.lockscreen

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.coldtea.moin.databinding.ActivityLockScreenAlarmBinding
import de.coldtea.moin.extensions.activateLockScreen
import de.coldtea.moin.extensions.deactivateLockScreen
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LockScreenAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockScreenAlarmBinding
    private val viewModel: LockScreenAlarmViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityLockScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStateObserver()
        observeSavedAlarmInfo()

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
        dismiss.setOnClickListener {
            viewModel.dismissAlarm()
        }

        snooze.setOnClickListener {
            viewModel.snoozeAlarm()
        }
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
                is Done -> {
                    finish()
                }
            }
        }
    }

    private fun observeSavedAlarmInfo() = lifecycleScope.launch {
        viewModel.label.collect {
            binding.label.text = it
        }
    }

}