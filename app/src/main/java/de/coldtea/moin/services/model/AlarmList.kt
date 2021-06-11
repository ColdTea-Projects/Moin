package de.coldtea.moin.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmList(
    val alarmItems: List<AlarmItem>
)