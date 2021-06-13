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
import timber.log.Timber

class SmplrAlarmService(private val context: Context) {

    private var smplrAlarmListRequestAPI: SmplrAlarmListRequestAPI? = null

    val _alarmList = MutableSharedFlow<AlarmList>()
    val alarmList: SharedFlow<AlarmList> = _alarmList

    init {
        smplrAlarmChangeOrRequestListener(context){
            it.convertToAlarmList()?.let { alarmList ->
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("Moin --> _alarmList.emit(alarmList) ")
                    _alarmList.emit(alarmList)
                }
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
        if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI}
        // intent { intent }
        //receiverIntent { receiverIntent }
    }

    fun cancelAlarm(requestId: Int){
        smplrAlarmCancel(context){
            requestCode { requestId }
            if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI}
        }
    }

    fun callRequestAlarmList() {
        Timber.d("Moin --> callRequestAlarmList ")
        smplrAlarmListRequestAPI?.requestAlarmList()
    }


}