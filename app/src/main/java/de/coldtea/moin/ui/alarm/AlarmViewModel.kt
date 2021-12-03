package de.coldtea.moin.ui.alarm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.R
import de.coldtea.moin.domain.model.alarm.AlarmList
import de.coldtea.moin.domain.receivers.ActionReceiver
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.extensions.getTimeExactForAlarm
import de.coldtea.smplr.smplralarm.models.WeekDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import java.util.Calendar.MILLISECOND
import java.util.Calendar.SECOND

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

    fun getRemainingTimeText(
        hour: Int,
        minute: Int,
        weekDays: List<WeekDays>
    ): String{
        val cal = Calendar
            .getInstance()

        cal.set(SECOND, 0)
        cal.set(MILLISECOND, 0)

        val nowInMillis = cal.timeInMillis

        val alarmTimeInMillis = Calendar
            .getInstance()
            .getTimeExactForAlarm(hour, minute, weekDays)
            .timeInMillis

        val remainingTimeInMillis = alarmTimeInMillis - nowInMillis

        val days: Long = (remainingTimeInMillis / DAY_IN_MILLIS) - 1
        val hours: Long = remainingTimeInMillis / HOUR_IN_MILLIS  % 24
        val minutes: Long = remainingTimeInMillis / MIN_IN_MILLIS % 60

        var remainingTimeText = StringBuilder()

        if(days > 0) remainingTimeText.append("$days days")
        if(hours > 0) remainingTimeText.append(" $hours hours")
        remainingTimeText.append(" $minutes minutes")

        return remainingTimeText.toString()
    }

    companion object {
        internal const val ACTION_SNOOZE = "action_snooze"
        internal const val ACTION_DISMISS = "action_dismiss"
        internal const val HOUR = "hour"
        internal const val MINUTE = "minute"

        private const val MIN_IN_MILLIS = 60000
        private const val HOUR_IN_MILLIS = 60 * MIN_IN_MILLIS
        private const val DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS
    }
}