package de.coldtea.moin.ui.alarm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.R
import de.coldtea.moin.domain.model.alarm.AlarmList
import de.coldtea.moin.domain.receivers.ActionReceiver
import de.coldtea.moin.domain.services.SmplrAlarmService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val smplrAlarmService: SmplrAlarmService
) : ViewModel() {

    private val _alarmList = MutableSharedFlow<AlarmList>()
    val alarmList: SharedFlow<AlarmList> = _alarmList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.alarmList.collect {
                _alarmList.emit(it.alarmList)
            }
        }
    }

    fun setAlarm(hour: Int, minute: Int, applicationContext: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            val snoozeIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
                action = ACTION_SNOOZE
                putExtra(HOUR, hour)
                putExtra(MINUTE, minute)
            }

            val dismissIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
                action = ACTION_DISMISS
            }

            smplrAlarmService.setAlarm(
                hour = hour,
                minute = minute,
                smallIcon = R.drawable.ic_baseline_alarm_24,
                weekDays = listOf(),
                label = "",
                snooze = snoozeIntent,
                dismiss = dismissIntent
            )
        }

    fun getAlarms() = smplrAlarmService.callRequestAlarmList()


    companion object {
        internal const val ACTION_SNOOZE = "action_snooze"
        internal const val ACTION_DISMISS = "action_dismiss"
        internal const val HOUR = "hour"
        internal const val MINUTE = "minute"
    }
}