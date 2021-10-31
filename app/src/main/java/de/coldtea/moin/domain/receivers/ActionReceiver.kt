package de.coldtea.moin.domain.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.moin.domain.model.alarm.AlarmItem
import de.coldtea.moin.domain.model.alarm.DismissAlarmRequest
import de.coldtea.moin.domain.model.alarm.DismissAlarmUpdate
import de.coldtea.moin.domain.model.alarm.SnoozeAlarmUpdate
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

    private val mainCoroutineScope
        get() = CoroutineScope(Dispatchers.Main + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin.RingerService --> mainCoroutineScope crashed: $t")
        })

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_NOTIFICATION_ID, -1)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mainCoroutineScope.launch(Dispatchers.IO) {

            smplrAlarmService.alarmList.collect { alarmObject ->

                when (alarmObject.alarmEvent) {
                    SnoozeAlarmUpdate,
                    DismissAlarmUpdate -> ringerService.stop()
                    DismissAlarmRequest -> alarmObject.alarmList.alarmItems
                        .find { alarmItem -> alarmItem.requestId == notificationId }
                        ?.let { alarmItem ->
                            setAlarmForDismissal(alarmItem, notificationId)
                        }
                }
            }
        }

        if(intent.action == ACTION_SNOOZE){
            notificationManager.cancel(notificationId)
            mainCoroutineScope.launch(Dispatchers.IO) {
                smplrAlarmService.updateAlarm(
                    requestId = notificationId,
                    hour = snoozeTime.first,
                    minute = snoozeTime.second,
                    isActive = true,
                    alarmEvent = SnoozeAlarmUpdate
                )
            }.also {
                ringerService.stop()
            }
        }
        if (intent.action == ACTION_DISMISS){
            notificationManager.cancel(notificationId)
            mainCoroutineScope.launch(Dispatchers.IO) {
                smplrAlarmService.callRequestAlarmList(DismissAlarmRequest)
            }
        }
    }

    private val snoozeTime: Pair<Int, Int>
        get() = Calendar.getInstance().let {
            it.add(Calendar.MINUTE, 15)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }

    private fun setAlarmForDismissal(alarmItem: AlarmItem, requestId: Int) {
        smplrAlarmService.updateAlarm(
            requestId = requestId,
            hour = alarmItem.info.originalHour.toIntOrNull(),
            minute = alarmItem.info.originalMinute.toIntOrNull(),
            isActive = false,
            alarmEvent = DismissAlarmUpdate
        )
    }

}