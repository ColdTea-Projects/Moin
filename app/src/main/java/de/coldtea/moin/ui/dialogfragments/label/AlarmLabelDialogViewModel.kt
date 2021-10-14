package de.coldtea.moin.ui.dialogfragments.label

import androidx.lifecycle.ViewModel
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem

class AlarmLabelDialogViewModel(
    private val smplrAlarmService: SmplrAlarmService
): ViewModel() {

    fun saveAlarm(alarmDelegateItem: AlarmDelegateItem, newLabel: String){
        val infoPairs = listOf(
            "originalHour" to "${alarmDelegateItem.originalHour}",
            "originalMinute" to "${alarmDelegateItem.originalMinute}",
            "isExpanded" to "${alarmDelegateItem.isExpanded}",
            "label" to newLabel
        )

        smplrAlarmService.updateAlarm(
            requestId = alarmDelegateItem.requestId,
            hour = alarmDelegateItem.hour,
            minute = alarmDelegateItem.minute,
            weekDays = alarmDelegateItem.weekDays,
            isActive = alarmDelegateItem.isActive,
            infoPairs = infoPairs
        )
    }
}