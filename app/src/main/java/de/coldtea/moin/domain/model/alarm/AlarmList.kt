package de.coldtea.moin.domain.model.alarm

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmList(
    val alarmItems: List<AlarmItem>
)