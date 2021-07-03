package de.coldtea.moin.ui.alarm.adapter.model

import de.coldtea.smplr.smplralarm.models.WeekDays

data class AlarmDelegateItem(
    val requestId: Int,
    var hour: Int,
    var minute: Int,
    var weekDays: List<WeekDays>,
    var isActive: Boolean,
    var isExpanded: Boolean = false,
    var originalHour: Int,
    var originalMinute: Int
)
