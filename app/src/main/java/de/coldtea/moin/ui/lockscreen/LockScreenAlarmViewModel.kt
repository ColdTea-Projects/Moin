
package de.coldtea.moin.ui.lockscreen

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.domain.model.alarm.*
import de.coldtea.moin.domain.model.ringer.Plays
import de.coldtea.moin.domain.model.ringer.Randomized
import de.coldtea.moin.domain.model.ringer.Stops
import de.coldtea.moin.domain.services.RingerService
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.LockScreenState
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class LockScreenAlarmViewModel(
    private val ringerService: RingerService,
    private val smplrAlarmService: SmplrAlarmService
) : ViewModel() {
    var requestId = -1

    private val _lockScreenState = MutableStateFlow<LockScreenState>(Ringing)
    val lockScreenState: StateFlow<LockScreenState> = _lockScreenState

    private val _label = MutableSharedFlow<String>()
    val label: SharedFlow<String> = _label

    init {
        viewModelScope.launch(Dispatchers.IO) {

            smplrAlarmService.alarmList.collect { alarmObject ->

                when (alarmObject.alarmEvent) {
                    SnoozeAlarmUpdate,
                    DismissAlarmUpdate -> ringerService.stop()
                    DismissAlarmRequest -> alarmObject.alarmList.alarmItems
                        .find { alarmItem -> alarmItem.requestId == requestId }
                        ?.let { alarmItem ->
                            setAlarmForDismissal(alarmItem)
                        }
                    RequestAlarms -> _label.emit(alarmObject.alarmList.alarmItems[0].info.label)
                }
            }
        }

        viewModelScope.launch {

            ringerService.ringerStateInfo.collect { ringerStateInfo ->

                when(ringerStateInfo){
                    is Plays -> {
                        //TODO add info to screen
                    }
                    is Randomized -> {
                        //TODO add info to screen
                    }
                    Stops -> {
                        _lockScreenState.emit(Done)
                    }
                }
            }
        }
    }

    fun ring() = ringerService.ring()

    fun dismissAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.callRequestAlarmList(DismissAlarmRequest)
        }
    }

    fun snoozeAlarm() =
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.updateAlarm(
                requestId = requestId,
                hour = snoozeTime.first,
                minute = snoozeTime.second,
                isActive = true,
                alarmEvent = SnoozeAlarmUpdate
            )
        }.also {
            ringerService.stop()
        }

    fun requestAlarmForUI() = smplrAlarmService.callRequestAlarmList(
        alarmEvent = RequestAlarms
    )

    fun onScreenDestroyed(context: Context){
        ringerService.stop()
        ringerService.invalidate()
        dismissNotification(context)
    }

    private fun setAlarmForDismissal(alarmItem: AlarmItem) {
        smplrAlarmService.updateAlarm(
            requestId = requestId,
            hour = alarmItem.info.originalHour.toIntOrNull(),
            minute = alarmItem.info.originalMinute.toIntOrNull(),
            isActive = false,
            alarmEvent = DismissAlarmUpdate
        )
    }

    private val snoozeTime: Pair<Int, Int>
        get() = Calendar.getInstance().let {
            it.add(Calendar.MINUTE, 15)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }

    private fun dismissNotification(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(requestId)
    }
}