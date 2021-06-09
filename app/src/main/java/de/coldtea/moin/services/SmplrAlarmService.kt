package de.coldtea.moin.services

import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.smplrAlarmChangeOrRequestListener
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SmplrAlarmService(private val context: Context) {

    var smplrAlarmListRequestAPI: SmplrAlarmListRequestAPI? = null

    private val _alarmListJson = MutableSharedFlow<String>()
    val alarmListJson: SharedFlow<String> = _alarmListJson

    init {
        smplrAlarmChangeOrRequestListener(context){
            _alarmListJson.tryEmit(it)
        }.also {
            smplrAlarmListRequestAPI = it
        }
    }

    fun setAlarm(hour: Int, minute: Int, notificationItem: NotificationItem,intent: Intent? = null, receiverIntent: Intent? = null, weekDays: List<WeekDays>? = null): Int = smplrAlarmSet(context = context){
        hour { hour }
        min { minute }
        if (weekDays != null) weekdays { weekDays }
        notification { notificationItem }
        // intent { intent }
        //receiverIntent { receiverIntent }
    }

    fun callRequestAlarmList(): Unit? = smplrAlarmListRequestAPI?.requestAlarmList()


}