package de.coldtea.moin.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.R
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AlarmViewModel: ViewModel(){

    val smplrAlarmService: SmplrAlarmService by inject(SmplrAlarmService::class.java)

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
                hour = 22, minute = 33, notificationItem = notificationItem, weekDays = listOf(WeekDays.WEDNESDAY, WeekDays.SUNDAY)
            )

            delay(100)
            smplrAlarmService.callRequestAlarmList()
        }

    }

    fun getAlarms() = smplrAlarmService.callRequestAlarmList()
 }