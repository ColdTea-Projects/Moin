package de.coldtea.moin.ui.alarm.lockscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
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
import timber.log.Timber


class LockScreenAlarmActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLockScreenAlarmBinding
    private val viewModel: LockScreenAlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            initStateObserver()
        }
    }

    override fun onStart() {
        super.onStart()
        setupUIItems()
        activateLockScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        deactivateLockScreen()
    }

    private fun setupUIItems() = with(binding){
        dismiss.setOnClickListener {
            finish()
        }

        snooze.setOnClickListener {
            viewModel.snoozeAlarm()
        }
    }

    private suspend fun initStateObserver() = viewModel.lockScreenState.collect { state ->
        when(state){
            is Ringing -> {
                viewModel.requestId = intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID, -1)
            }
            is Done -> {
                finish()
            }
        }
    }

}