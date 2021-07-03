package de.coldtea.moin.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.R
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.services.model.AlarmList
import de.coldtea.smplr.smplralarm.models.NotificationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AlarmViewModel: ViewModel(){

    private val smplrAlarmService: SmplrAlarmService by inject(SmplrAlarmService::class.java)

    private val _alarmList = MutableSharedFlow<AlarmList>()
    val alarmList: SharedFlow<AlarmList> = _alarmList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.alarmList.collect{
                _alarmList.emit(it.alarmList)
            }
        }
    }

    fun setAlarm(hour: Int, minute: Int){
        val notificationItem = NotificationItem(
            R.drawable.ic_baseline_alarm_24,
            "Alarm is ringing",
            "Alarm is ringing",
            "",
            true,
            null, null, null, null
        )

        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.setAlarm(
                hour = hour, minute = minute, notificationItem = notificationItem, weekDays = listOf()
            )
        }

    }

    fun getAlarms() = smplrAlarmService.callRequestAlarmList()
 }