package de.coldtea.moin.ui.alarm.adapter.model

data class AlarmBundle(
    val id: Int,
    val alarmDelegateItem: AlarmDelegateItem,
    val onClickLabel: (AlarmDelegateItem) -> Unit,
    val onTimeSet: (AlarmDelegateItem) -> Unit
)