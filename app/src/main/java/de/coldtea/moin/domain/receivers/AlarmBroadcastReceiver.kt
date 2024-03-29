package de.coldtea.moin.domain.receivers

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.moin.domain.model.alarm.NotificationListenerRequest
import de.coldtea.moin.domain.model.ringer.Stops
import de.coldtea.moin.domain.services.RingerService
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class AlarmBroadcastReceiver: BroadcastReceiver() {
    private var requestId = -1
    private val smplrAlarmService: SmplrAlarmService by KoinJavaComponent.inject(SmplrAlarmService::class.java)
    private val ringerService: RingerService by KoinJavaComponent.inject(RingerService::class.java)
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
        Timber.d("Moin.AlarmBroadcastReceiver --> ioCoroutineScope crashed: $t")
    })

    private var observeAlarmListJob: Job? = null
    private val mainCoroutineScope
        get() = CoroutineScope(Dispatchers.Main + CoroutineExceptionHandler { _, t ->
            Timber.d("Moin.RingerService --> mainCoroutineScope crashed: $t")
        })

    override fun onReceive(context: Context?, intent: Intent?) {
        requestId = intent?.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID, -1)?:return
        Timber.d("Moin.AlarmBroadcastReceiver --> alarm received: $requestId")
        if (requestId == -1) return

        onNotificationPosted(context)
    }

    fun onNotificationPosted(applicationContext: Context?) {
        val keyguardManager = applicationContext?.getSystemService(Context.KEYGUARD_SERVICE)as KeyguardManager

        //is screen not locked
        if (!keyguardManager.isKeyguardLocked){
            Timber.d("Moin.AlarmBroadcastReceiver --> onNotificationPosted")
            observeAlarmListJob = observeAlarmList()
            observeRingerState(applicationContext)
            smplrAlarmService.callRequestAlarmList(NotificationListenerRequest)
        }
    }

    private fun observeAlarmList() = ioCoroutineScope.launch {
        smplrAlarmService.alarmList.collect { alarmObject ->

            if (alarmObject.alarmEvent is NotificationListenerRequest) {
                alarmObject.alarmList.alarmItems.find { it.requestId == requestId  }?.let {
                    ringerService.ring()
                }
            }
        }
    }

    private fun observeRingerState(applicationContext: Context) = mainCoroutineScope.launch {
        ringerService.ringerStateInfo.collect { ringerStateInfo ->
            if(ringerStateInfo is Stops){
                dismissNotification(applicationContext)
            }
        }
    }

    private fun dismissNotification(applicationContext: Context){
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(requestId)
        observeAlarmListJob?.cancel()
    }
}