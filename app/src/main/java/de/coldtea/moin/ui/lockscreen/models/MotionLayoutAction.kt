package de.coldtea.moin.ui.lockscreen.models

sealed class MotionLayoutAction

object OnSnoozeDrag: MotionLayoutAction()
object OnDismissDrag: MotionLayoutAction()