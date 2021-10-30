package de.coldtea.moin.ui.alarm.lockscreen.models

sealed class LockScreenState

object Ringing: LockScreenState()
object Done: LockScreenState()