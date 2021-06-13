package de.coldtea.moin.services

import android.content.Context
import android.content.Intent
import de.coldtea.moin.extensions.convertToAlarmList
import de.coldtea.moin.services.model.AlarmList
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.smplrAlarmCancel
import de.coldtea.smplr.smplralarm.smplrAlarmChangeOrRequestListener
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SmplrAlarmService(private val context: Context) {

    var smplrAlarmListRequestAPI: SmplrAlarmListRequestAPI? = null

    private val _alarmList = MutableSharedFlow<AlarmList>()
    val alarmList: SharedFlow<AlarmList> = _alarmList

    init {
        smplrAlarmChangeOrRequestListener(context){
            it.convertToAlarmList()?.let { alarmList ->
                CoroutineScope(Dispatchers.IO).launch { _alarmList.emit(alarmList) }
            }
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

    fun cancelAlarm(requestId: Int){
        smplrAlarmCancel(context){
            requestCode { requestId }
        }
    }

    fun callRequestAlarmList(): Unit? = smplrAlarmListRequestAPI?.requestAlarmList()


}