package de.coldtea.moin.services.model

sealed class AlarmEvent

object SnoozeAlarmUpdate : AlarmEvent()
object DismissAlarmRequest : AlarmEvent()
object DismissAlarmUpdate : AlarmEvent()