package de.coldtea.moin.ui.alarm.adapter.model

import de.coldtea.smplr.smplralarm.models.WeekDays

data class AlarmDelegateItem(
    val requestId: Int,
    val hour: Int,
    val minute: Int,
    val weekDays: List<WeekDays>,
    val isActive: Boolean,
    var isExpanded: Boolean = false,
)