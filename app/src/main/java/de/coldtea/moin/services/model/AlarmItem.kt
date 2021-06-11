package de.coldtea.moin.services.model

import com.squareup.moshi.JsonClass
import de.coldtea.smplr.smplralarm.models.WeekDays

@JsonClass(generateAdapter = true)
data class AlarmItem(
    val requestId: Int,
    val hour: Int,
    val minute: Int,
    val weekDays: List<WeekDays>,
    val isActive: Boolean
)