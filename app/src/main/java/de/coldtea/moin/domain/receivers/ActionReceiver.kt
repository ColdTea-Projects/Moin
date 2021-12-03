package de.coldtea.moin.domain.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.domain.model.alarm.*
import de.coldtea.moin.domain.model.extensions.onAlarmObjectReceived
import de.coldtea.moin.domain.services.RingerService
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.ui.alarm.AlarmViewModel.Companion.ACTION_DISMISS
import de.coldtea.moin.ui.alarm.AlarmViewModel.Companion.ACTION_SNOOZE
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.util.*

class ActionReceiver: BroadcastReceiver() {
    private val smplrAlarmService: SmplrAlarmService by KoinJavaComponent.inject(SmplrAlarmService::class.java)
    private val ringerService: RingerService by KoinJavaComponent.inject(RingerService::class.java)
    private val sharedPreferencesRepository: SharedPreferencesRepository by KoinJavaComponent.inject(SharedPreferencesRepository::class.java)

    private val ioCoroutineScope
        get() = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin.RingerService --> mainCoroutineScope crashed: $t")
        })

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_NOTIFICATION_ID, -1)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        ioCoroutineScope.launch {

            smplrAlarmService.alarmList.collect { alarmObject ->
                when (alarmObject.alarmEvent) {
                    SnoozeAlarmUpdate,
                    DismissAlarmUpdate -> ringerService.stop()
                    SnoozeAlarmRequest -> alarmObject.onAlarmObjectReceived(notificationId){ updateAlarmForSnooze(it) }
                    DismissAlarmRequest -> alarmObject.onAlarmObjectReceived(notificationId){ updateAlarmForDismissal(it) }
                }
            }
        }

        if(intent.action == ACTION_SNOOZE){
            notificationManager.cancel(notificationId)
            ioCoroutineScope.launch {
                smplrAlarmService.callRequestAlarmList(SnoozeAlarmRequest)
            }
        }
        if (intent.action == ACTION_DISMISS){
            notificationManager.cancel(notificationId)
            ioCoroutineScope.launch {
                smplrAlarmService.callRequestAlarmList(DismissAlarmRequest)
            }
        }
    }

    private fun updateAlarmForDismissal(alarmItem: AlarmItem) {
        smplrAlarmService.updateAlarm(
            requestId = alarmItem.requestId,
            hour = alarmItem.info.originalHour.toIntOrNull(),
            minute = alarmItem.info.originalMinute.toIntOrNull(),
            isActive = alarmItem.weekDays.isNotEmpty(),
            alarmEvent = DismissAlarmUpdate,
            weekDays = alarmItem.weekDays
        )
    }

    fun updateAlarmForSnooze(alarmItem: AlarmItem) {
        val snoozeTime = getSnoozeTime(alarmItem.hour, alarmItem.minute)

        ioCoroutineScope.launch {
            smplrAlarmService.updateAlarm(
                requestId = alarmItem.requestId,
                hour = snoozeTime.first,
                minute = snoozeTime.second,
                isActive = true,
                alarmEvent = SnoozeAlarmUpdate,
                weekDays = alarmItem.weekDays
            )
        }.also {
            ringerService.stop()
        }
    }

    private fun getSnoozeTime(hour: Int?, minute: Int?): Pair<Int, Int> =
        Calendar.getInstance().let {
            if (hour != null) it.set(Calendar.HOUR_OF_DAY, hour)
            if (minute != null) it.set(Calendar.MINUTE, minute)

            it.add(Calendar.MINUTE, sharedPreferencesRepository.snoozeDuration)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }

}