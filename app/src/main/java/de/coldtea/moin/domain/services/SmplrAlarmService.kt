package de.coldtea.moin.domain.services

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.moin.MainActivity
import de.coldtea.moin.R
import de.coldtea.moin.domain.model.alarm.AlarmEvent
import de.coldtea.moin.domain.model.alarm.AlarmObject
import de.coldtea.moin.domain.model.alarm.SnoozeAlarmUpdate
import de.coldtea.moin.domain.receivers.AlarmBroadcastReceiver
import de.coldtea.moin.extensions.convertToAlarmList
import de.coldtea.moin.ui.lockscreen.LockScreenAlarmActivity
import de.coldtea.smplr.smplralarm.*
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.models.WeekDays
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

    private var lastAlarmEvent: AlarmEvent? = null

    private val onClickIntent = Intent(
        context.applicationContext,
        MainActivity::class.java
    )

    private val fullScreenIntent = Intent(
        context.applicationContext,
        LockScreenAlarmActivity::class.java
    )

    private val alarmReceivedIntent = Intent(
        context.applicationContext,
        AlarmBroadcastReceiver::class.java
    )

    init {
        smplrAlarmChangeOrRequestListener(context) {
            it.convertToAlarmList().let { alarmList ->
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("Moin --> _alarmList.emit(alarmList) -- $lastAlarmEvent")
                    _alarmList.emit(AlarmObject(alarmList, lastAlarmEvent))
                }
            }
        }.also {
            smplrAlarmListRequestAPI = it
        }

    }

    fun setAlarm(
        hour: Int,
        minute: Int,
        @DrawableRes smallIcon: Int,
        weekDays: List<WeekDays>? = null,
        alarmEvent: AlarmEvent? = null,
        label: String,
        snooze: Intent,
        dismiss: Intent
    ): Int = smplrAlarmSet(context = context) {

        hour { hour }
        min { minute }

        if (weekDays != null) weekdays {
            if (WeekDays.MONDAY in weekDays) monday()
            if (WeekDays.TUESDAY in weekDays) tuesday()
            if (WeekDays.WEDNESDAY in weekDays) wednesday()
            if (WeekDays.THURSDAY in weekDays) thursday()
            if (WeekDays.FRIDAY in weekDays) friday()
            if (WeekDays.SATURDAY in weekDays) saturday()
            if (WeekDays.SUNDAY in weekDays) sunday()
        }

        notification {
            alarmNotification {
                smallIcon { smallIcon }
                title { context.getString(R.string.alarm_title) }
                message { label }
                bigText { label }
                autoCancel { true }
                firstButtonText { context.getString(R.string.snooze_button) }
                secondButtonText { context.getString(R.string.dismiss_button) }
                firstButtonIntent { snooze }
                secondButtonIntent { dismiss }
                notificationDismissedIntent { dismiss }
            }
        }

        if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI }

        intent { onClickIntent }
        receiverIntent { fullScreenIntent }
        alarmReceivedIntent { alarmReceivedIntent }
        infoPairs {
            listOf(
                "originalHour" to "$hour",
                "originalMinute" to "$minute",
                "isExpanded" to "false",
                "label" to label
            )
        }

    }.also {
        lastAlarmEvent = alarmEvent
    }

    fun cancelAlarm(requestId: Int, alarmEvent: AlarmEvent? = null) {
        lastAlarmEvent = alarmEvent
        smplrAlarmCancel(context) {
            requestCode { requestId }
            if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI }
        }

    }

    fun updateAlarm(
        requestId: Int,
        hour: Int? = null,
        minute: Int? = null,
        weekDays: List<WeekDays>? = null,
        isActive: Boolean? = null,
        infoPairs: List<Pair<String, String>>? = null,
        alarmEvent: AlarmEvent? = null
    ) {
        lastAlarmEvent = alarmEvent

        smplrAlarmUpdate(context) {
            requestCode { requestId }
            if (hour != null) hour { hour }
            if (minute != null) min { minute }
            if (weekDays != null) weekdays {
                if (WeekDays.MONDAY in weekDays) monday()
                if (WeekDays.TUESDAY in weekDays) tuesday()
                if (WeekDays.WEDNESDAY in weekDays) wednesday()
                if (WeekDays.THURSDAY in weekDays) thursday()
                if (WeekDays.FRIDAY in weekDays) friday()
                if (WeekDays.SATURDAY in weekDays) saturday()
                if (WeekDays.SUNDAY in weekDays) sunday()
            } else {
                listOf<WeekDays>()
            }
            if (isActive != null) isActive { isActive }
            if (smplrAlarmListRequestAPI != null) requestAPI { smplrAlarmListRequestAPI as SmplrAlarmListRequestAPI }
            if (infoPairs != null) infoPairs { infoPairs }

            infoPairs?.getLabel()?.let { label ->
                notification {
                    alarmNotification {
                        message { label }
                        bigText { label }
                    }
                }
            }
        }
        lastAlarmEvent = SnoozeAlarmUpdate

    }

    fun callRequestAlarmList(alarmEvent: AlarmEvent? = null) {
        lastAlarmEvent = alarmEvent
        Timber.d("Moin --> callRequestAlarmList ")
        smplrAlarmListRequestAPI?.requestAlarmList()

    }

    private fun List<Pair<String, String>>.getLabel(): String? = this.find {
        it.first == "label"
    }?.second

}