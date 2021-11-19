package de.coldtea.moin.domain.model.alarm

sealed class AlarmEvent

object NotificationListenerRequest : AlarmEvent()
object SnoozeAlarmRequest : AlarmEvent()
object SnoozeAlarmUpdate : AlarmEvent()
object DismissAlarmRequest : AlarmEvent()
object DismissAlarmUpdate : AlarmEvent()
object RequestAlarms : AlarmEvent()