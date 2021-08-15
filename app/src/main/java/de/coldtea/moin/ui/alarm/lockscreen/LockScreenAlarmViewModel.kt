package de.coldtea.moin.ui.alarm.lockscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.services.model.AlarmItem
import de.coldtea.moin.services.model.DismissAlarmRequest
import de.coldtea.moin.services.model.DismissAlarmUpdate
import de.coldtea.moin.services.model.SnoozeAlarmUpdate
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.LockScreenState
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.*

class LockScreenAlarmViewModel : ViewModel() {

    private val smplrAlarmService: SmplrAlarmService by inject(SmplrAlarmService::class.java)
    var requestId = -1

    private val _lockScreenState = MutableStateFlow<LockScreenState>(Ringing)
    val lockScreenState: StateFlow<LockScreenState> = _lockScreenState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.alarmList.collect { alarmObject ->
                when (alarmObject.alarmEvent) {
                    SnoozeAlarmUpdate,
                    DismissAlarmUpdate -> _lockScreenState.emit(Done)
                    DismissAlarmRequest -> alarmObject.alarmList.alarmItems
                        .find { alarmItem ->  alarmItem.requestId == requestId }
                        ?.let { alarmItem ->
                            setAlarmForDismissal(alarmItem)
                        }
                }

            }
        }
    }

    fun dismissAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.callRequestAlarmList(DismissAlarmRequest)
        }
    }

    private fun setAlarmForDismissal(alarmItem: AlarmItem) {
        smplrAlarmService.updateAlarm(
            requestId = requestId,
            hour = alarmItem.info?.originalHour?.toIntOrNull(),
            minute = alarmItem.info?.originalMinute?.toIntOrNull(),
            isActive = false,
            alarmEvent = DismissAlarmUpdate
        )
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
        }

    private val snoozeTime: Pair<Int, Int>
        get() = Calendar.getInstance().let {
            it.add(Calendar.MINUTE, 15)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }
}