package de.coldtea.moin.domain.services

import android.content.Context
import android.content.Intent
import de.coldtea.moin.domain.model.alarm.AlarmEvent
import de.coldtea.moin.domain.model.alarm.AlarmObject
import de.coldtea.moin.domain.model.alarm.SnoozeAlarmUpdate
import de.coldtea.moin.extensions.convertToAlarmList
import de.coldtea.moin.ui.alarm.AlarmFragment
import de.coldtea.moin.ui.alarm.lockscreen.LockScreenAlarmActivity
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.smplrAlarmCancel
import de.coldtea.smplr.smplralarm.smplrAlarmChangeOrRequestListener
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import de.coldtea.smplr.smplralarm.smplrAlarmUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SmplrAlarmService(private val context: Context) {

    private var smplrAlarmListRequestAPI: SmplrAlarmListRequestAPI? = null

    private val _alarmList = MutableSharedFlow<AlarmObject>()
    val alarmList: SharedFlow<AlarmObject> = _alarmList

    private var lastAlarmEvent : AlarmEvent? = null

    private val onClickIntent = Intent(
        context,
        AlarmFragment::class.java
    )

    private val fullScreenIntent = Intent(
        context,
        LockScreenAlarmActivity::class.java
    )

    init {
        smplrAlarmChangeOrRequestListener(context){
            it.convertToAlarmList()?.let { alarmList ->
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("Moin --> _alarmList.emit(alarmList) ")
                    _alarmList.emit(AlarmObject(alarmList, lastAlarmEvent))
                }
            }
        }.also {
            smplrAlarmListRequestAPI = it
        }

    }

    fun setAlarm(hour: Int, minute: Int, notificationItem: NotificationItem, weekDays: List<WeekDays>? = null, alarmEvent: AlarmEvent? = null, label: String): Int {
        lastAlarmEvent = alarmEvent

        return smplrAlarmSet(context = context){
            hour { hour }
            min { minute }
            if (weekDays != null) weekdays {
                if(WeekDays.MONDAY in weekDays) monday()
                if(WeekDays.TUESDAY in weekDays) tuesday()
                if(WeekDays.WEDNESDAY in weekDays) wednesday()
                if(WeekDays.THURSDAY in weekDays) thursday()
                if(WeekDays.FRIDAY in weekDays) friday()
                if(WeekDays.SATURDAY in weekDays) saturday()
                if(WeekDays.SUNDAY in weekDays) sunday()
            }
            notification { notificationItem }
            if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI}
            intent { onClickIntent }
            receiverIntent{ fullScreenIntent }
            infoPairs {
                listOf(
                    "originalHour" to "$hour",
                    "originalMinute" to "$minute",
                    "isExpanded" to "false",
                    "label" to label
                )
            }

        }
    }

    fun cancelAlarm(requestId: Int, alarmEvent: AlarmEvent? = null){
        lastAlarmEvent = alarmEvent
        smplrAlarmCancel(context){
            requestCode { requestId }
            if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI}
        }

    }

    fun updateAlarm(requestId: Int, hour: Int? = null, minute: Int? = null, weekDays: List<WeekDays>? = null, isActive: Boolean? = null, infoPairs: List<Pair<String, String>>? = null, alarmEvent: AlarmEvent? = null){
        lastAlarmEvent = alarmEvent

        smplrAlarmUpdate(context){
            requestCode { requestId }
            if(hour != null) hour { hour }
            if(minute != null) min { minute }
            if (weekDays != null) weekdays {
                if(WeekDays.MONDAY in weekDays) monday()
                if(WeekDays.TUESDAY in weekDays) tuesday()
                if(WeekDays.WEDNESDAY in weekDays) wednesday()
                if(WeekDays.THURSDAY in weekDays) thursday()
                if(WeekDays.FRIDAY in weekDays) friday()
                if(WeekDays.SATURDAY in weekDays) saturday()
                if(WeekDays.SUNDAY in weekDays) sunday()
            }else {
                listOf<WeekDays>()
            }
            if(isActive != null) isActive { isActive }
            if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI}
            if (infoPairs != null) infoPairs { infoPairs }
        }
        lastAlarmEvent = SnoozeAlarmUpdate

    }

    fun callRequestAlarmList(alarmEvent: AlarmEvent? = null) {
        lastAlarmEvent = alarmEvent
        Timber.d("Moin --> callRequestAlarmList ")
        smplrAlarmListRequestAPI?.requestAlarmList()

    }


}