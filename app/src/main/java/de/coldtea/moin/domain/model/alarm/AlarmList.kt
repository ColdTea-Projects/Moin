package de.coldtea.moin.domain.model.alarm

import kotlinx.serialization.Serializable

@Serializable
data class AlarmList(
    val alarmItems: List<AlarmItem>
)