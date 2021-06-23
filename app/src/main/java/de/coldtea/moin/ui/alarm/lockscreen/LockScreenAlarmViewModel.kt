package de.coldtea.moin.ui.alarm.lockscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.R
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.moin.services.model.AlarmItem
import de.coldtea.moin.services.model.AlarmList
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.LockScreenState
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import de.coldtea.smplr.smplralarm.models.NotificationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
            smplrAlarmService.alarmList.collect {
                _lockScreenState.emit(Done)
            }
        }
    }

    fun snoozeAlarm() =
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.updateAlarm(
                requestId = requestId,
                hour = snoozeTime.first,
                minute = snoozeTime.second,
                isActive = true
            )
        }

    private val snoozeTime: Pair<Int, Int>
        get() = Calendar.getInstance().let {
            it.add(Calendar.MINUTE, 15)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }
}