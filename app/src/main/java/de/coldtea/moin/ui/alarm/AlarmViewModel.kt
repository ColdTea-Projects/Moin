package de.coldtea.moin.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.R
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.services.model.AlarmList
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AlarmViewModel: ViewModel(){

    val smplrAlarmService: SmplrAlarmService by inject(SmplrAlarmService::class.java)

    val _alarmList = MutableSharedFlow<AlarmList>()
    val alarmList: SharedFlow<AlarmList> = _alarmList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.alarmList.collect{
                _alarmList.emit(it)
            }
        }
    }

    fun testAlarm(){
        val notificationItem = NotificationItem(
            R.drawable.ic_baseline_alarm_24,
            "Welcome to MoinApp",
            "Welcome to MoinApp",
            "Welcome to MoinApp",
            true,
            null, null, null, null
        )

        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.setAlarm(
                hour = (Math.random()*23).toInt(), minute = (Math.random()*59).toInt(), notificationItem = notificationItem, weekDays = listOf(WeekDays.WEDNESDAY, WeekDays.SUNDAY)
            )
        }

    }

    fun getAlarms() = smplrAlarmService.callRequestAlarmList()
 }