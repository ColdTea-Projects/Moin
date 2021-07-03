package de.coldtea.moin.services.model

import com.squareup.moshi.JsonClass
import de.coldtea.moin.extensions.convertToInfoPairs
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem
import de.coldtea.smplr.smplralarm.models.WeekDays

@JsonClass(generateAdapter = true)
data class AlarmItem(
    val requestId: Int,
    val hour: Int,
    val minute: Int,
    val weekDays: List<WeekDays>,
    val isActive: Boolean,
    val infoPairs: String
){
    val info:InfoPairs?
        get() = infoPairs.convertToInfoPairs()
}

@JsonClass(generateAdapter = true)
data class InfoPairs(
    val originalHour: String,
    val originalMinute: String,
    val isExpanded: String
)

fun AlarmItem.convertToDelegateItem(): AlarmDelegateItem =
    AlarmDelegateItem(
        requestId = requestId,
        hour = hour,
        minute = minute,
        weekDays = weekDays,
        isActive = isActive,
        isExpanded = info?.isExpanded.toBoolean(),
        originalHour = info?.originalHour?.toIntOrNull()?:0,
        originalMinute = info?.originalMinute?.toIntOrNull()?:0
    )

