package de.coldtea.moin.services.model

data class AlarmObject(
    val alarmList: AlarmList,
    val alarmEvent: AlarmEvent? = null
)
