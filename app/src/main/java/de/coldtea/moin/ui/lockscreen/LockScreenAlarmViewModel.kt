package de.coldtea.moin.ui.lockscreen

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.domain.model.alarm.*
import de.coldtea.moin.domain.model.extensions.onAlarmObjectReceived
import de.coldtea.moin.domain.model.ringer.Plays
import de.coldtea.moin.domain.model.ringer.Randomized
import de.coldtea.moin.domain.model.ringer.Stops
import de.coldtea.moin.domain.services.RingerService
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.ui.alarm.lockscreen.models.AlarmItemReceived
import de.coldtea.moin.ui.alarm.lockscreen.models.Done
import de.coldtea.moin.ui.alarm.lockscreen.models.LockScreenState
import de.coldtea.moin.ui.alarm.lockscreen.models.Ringing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class LockScreenAlarmViewModel(
    private val ringerService: RingerService,
    private val smplrAlarmService: SmplrAlarmService,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {
    var requestId = -1

    private val _lockScreenState = MutableStateFlow<LockScreenState>(Ringing)
    val lockScreenState: StateFlow<LockScreenState> = _lockScreenState

    val isRinging
        get() = ringerService.isStartedPlaying

    init {
        viewModelScope.launch(Dispatchers.IO) {

            smplrAlarmService.alarmList.collect { alarmObject ->

                when (alarmObject.alarmEvent) {
                    SnoozeAlarmUpdate,
                    DismissAlarmUpdate -> ringerService.stop()
                    SnoozeAlarmRequest -> alarmObject
                        .onAlarmObjectReceived(requestId){ updateAlarmForSnooze(it) }
                    DismissAlarmRequest -> alarmObject
                        .onAlarmObjectReceived(requestId){ updateAlarmForDismissal(it) }
                    RequestAlarms -> alarmObject
                        .onAlarmObjectReceived(requestId){
                            emitAlarmItem(it)
                        }
                    else -> Timber.e("Moin --> Wrong state received at LockScreenAlarmViewModel.smplrAlarmService.alarmList.collect")

                }
            }
        }

        viewModelScope.launch {

            ringerService.ringerStateInfo.collect { ringerStateInfo ->

                when (ringerStateInfo) {
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

    fun snoozeAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.callRequestAlarmList(SnoozeAlarmRequest)
        }
    }

    fun requestAlarmForUI() = smplrAlarmService.callRequestAlarmList(
        alarmEvent = RequestAlarms
    )

    fun onScreenDestroyed(context: Context) {
        ringerService.stop()
        ringerService.invalidate()
        dismissNotification(context)
    }

    private fun updateAlarmForDismissal(alarmItem: AlarmItem) {
        smplrAlarmService.updateAlarm(
            requestId = requestId,
            hour = alarmItem.info.originalHour.toIntOrNull(),
            minute = alarmItem.info.originalMinute.toIntOrNull(),
            isActive = false,
            alarmEvent = DismissAlarmUpdate,
            weekDays = alarmItem.weekDays
        )
    }

    private fun updateAlarmForSnooze(alarmItem: AlarmItem) {
        val snoozeTime = getSnoozeTime(alarmItem.hour, alarmItem.minute)

        viewModelScope.launch(Dispatchers.IO) {
            smplrAlarmService.updateAlarm(
                requestId = requestId,
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

    private fun emitAlarmItem(alarmItem: AlarmItem) = viewModelScope.launch {
        _lockScreenState.emit(
            AlarmItemReceived(alarmItem)
        )
    }

    private fun getSnoozeTime(hour: Int?, minute: Int?): Pair<Int, Int> =
        Calendar.getInstance().let {
            if (hour != null) it.set(Calendar.HOUR_OF_DAY, hour)
            if (minute != null) it.set(Calendar.MINUTE, minute)

            it.add(Calendar.MINUTE, sharedPreferencesRepository.snoozeDuration)
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }

    private fun dismissNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(requestId)
    }
}