package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.domain.model.alarm.AlarmItem
import de.coldtea.moin.domain.model.alarm.AlarmObject

fun AlarmObject.onAlarmObjectReceived(
    requestId: Int,
    onReceived: (alarmItem: AlarmItem) -> Unit
) = this.alarmList.alarmItems
    .find { alarmItem -> alarmItem.requestId == requestId }
    ?.let { alarmItem ->
        onReceived(alarmItem)
    }