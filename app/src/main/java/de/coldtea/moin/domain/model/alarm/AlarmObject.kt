package de.coldtea.moin.domain.model.alarm

data class AlarmObject(
    val alarmList: AlarmList,
    val alarmEvent: AlarmEvent? = null
)
