package de.coldtea.moin.ui.diffutils

import androidx.recyclerview.widget.DiffUtil
import de.coldtea.moin.extensions.getTimeText
import de.coldtea.moin.ui.alarm.adapter.model.AlarmBundle

class AlarmsDiffUtilCallback : DiffUtil.ItemCallback<AlarmBundle>() {

    override fun areItemsTheSame(oldItem: AlarmBundle, newItem: AlarmBundle): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AlarmBundle, newItem: AlarmBundle): Boolean =
        oldItem.hashCode() == newItem.hashCode()

    override fun getChangePayload(oldItem: AlarmBundle, newItem: AlarmBundle): Any? {
        val changePayload: MutableMap<String, Any> = mutableMapOf()

        when {
            isAlarmChanged(oldItem, newItem) && !isAlarmSnoozed(newItem) -> {
                val hourMinute = newItem.alarmDelegateItem.hour to newItem.alarmDelegateItem.minute
                changePayload[KEY_TIME] = hourMinute.getTimeText()
            }
            isAlarmChanged(oldItem, newItem) && isAlarmSnoozed(newItem) -> {
                val hourMinute =
                    newItem.alarmDelegateItem.originalHour to newItem.alarmDelegateItem.originalMinute
                changePayload[KEY_TIME] = hourMinute.getTimeText()

                val snoozeHourMinute =
                    newItem.alarmDelegateItem.hour to newItem.alarmDelegateItem.minute
                changePayload[KEY_SNOOZETIME] = snoozeHourMinute.getTimeText()
            }
            isSnoozedCancelled(oldItem, newItem) -> changePayload[KEY_SNOOZETIME] = ""
        }

        if (oldItem.alarmDelegateItem.isActive != newItem.alarmDelegateItem.isActive) changePayload[KEY_ISACTIVE] =
            newItem.alarmDelegateItem.isActive
        if (oldItem.alarmDelegateItem.isExpanded != newItem.alarmDelegateItem.isExpanded) changePayload[KEY_ISEXPANDED] =
            newItem.alarmDelegateItem.isExpanded
        if (oldItem.alarmDelegateItem.originalMinute != newItem.alarmDelegateItem.originalMinute) changePayload[KEY_ORIGINALMINUTE] =
            newItem.alarmDelegateItem.originalMinute
        if (oldItem.alarmDelegateItem.label != newItem.alarmDelegateItem.label) changePayload[KEY_LABEL] =
            newItem.alarmDelegateItem.label
        if (oldItem.alarmDelegateItem.weekDays != newItem.alarmDelegateItem.weekDays) changePayload[KEY_WEEKDAYS] =
            newItem.alarmDelegateItem.weekDays

        return changePayload
    }

    private fun isSnoozedCancelled(oldItem: AlarmBundle, newItem: AlarmBundle) =
        oldItem.alarmDelegateItem.originalHour != newItem.alarmDelegateItem.originalHour
                || oldItem.alarmDelegateItem.originalMinute != newItem.alarmDelegateItem.originalMinute

    private fun isAlarmSnoozed(newItem: AlarmBundle): Boolean =
        newItem.alarmDelegateItem.hour != newItem.alarmDelegateItem.originalHour
                || newItem.alarmDelegateItem.minute != newItem.alarmDelegateItem.originalMinute

    private fun isAlarmChanged(oldItem: AlarmBundle, newItem: AlarmBundle): Boolean =
        oldItem.alarmDelegateItem.hour != newItem.alarmDelegateItem.hour
                || oldItem.alarmDelegateItem.minute != newItem.alarmDelegateItem.minute

    companion object {
        const val KEY_TIME = "time"
        const val KEY_SNOOZETIME = "snoozeTime"
        const val KEY_WEEKDAYS = "weekDays"
        const val KEY_ISACTIVE = "isActive"
        const val KEY_ISEXPANDED = "isExpanded"
        const val KEY_ORIGINALMINUTE = "originalMinute"
        const val KEY_LABEL = "label"
    }
}