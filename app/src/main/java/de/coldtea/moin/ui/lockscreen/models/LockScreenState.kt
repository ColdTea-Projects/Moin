package de.coldtea.moin.ui.alarm.lockscreen.models

import de.coldtea.moin.domain.model.alarm.AlarmItem
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo

sealed class LockScreenState

object Ringing: LockScreenState()
object Done: LockScreenState()

data class AlarmItemReceived(val alarmItem: AlarmItem): LockScreenState()
data class RingerScreenInfoReceived(val ringerScreenInfo: RingerScreenInfo): LockScreenState()
