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

        if (oldItem.alarmDelegateItem.hour != newItem.alarmDelegateItem.hour || oldItem.alarmDelegateItem.minute != newItem.alarmDelegateItem.minute) {
            val hourMinute =  newItem.alarmDelegateItem.hour to newItem.alarmDelegateItem.minute
            changePayload[KEY_TIME] = hourMinute.getTimeText()
        }
        if (oldItem.alarmDelegateItem.isActive != newItem.alarmDelegateItem.isActive) changePayload[KEY_ISACTIVE] = newItem.alarmDelegateItem.isActive
        if (oldItem.alarmDelegateItem.isExpanded != newItem.alarmDelegateItem.isExpanded) changePayload[KEY_ISEXPANDED] = newItem.alarmDelegateItem.isExpanded
        if (oldItem.alarmDelegateItem.originalHour != newItem.alarmDelegateItem.originalHour) changePayload[KEY_ORIGINALHOUR] = newItem.alarmDelegateItem.originalHour
        if (oldItem.alarmDelegateItem.originalMinute != newItem.alarmDelegateItem.originalMinute) changePayload[KEY_ORIGINALMINUTE] = newItem.alarmDelegateItem.originalMinute
        if (oldItem.alarmDelegateItem.label != newItem.alarmDelegateItem.label) changePayload[KEY_LABEL] = newItem.alarmDelegateItem.label
        if (oldItem.alarmDelegateItem.weekDays != newItem.alarmDelegateItem.weekDays) changePayload[KEY_WEEKDAYS] = newItem.alarmDelegateItem.weekDays

        return changePayload
    }

    companion object {
        const val KEY_TIME = "time"
        const val KEY_MINUTE = "minute"
        const val KEY_WEEKDAYS = "weekDays"
        const val KEY_ISACTIVE = "isActive"
        const val KEY_ISEXPANDED = "isExpanded"
        const val KEY_ORIGINALHOUR = "originalHour"
        const val KEY_ORIGINALMINUTE = "originalMinute"
        const val KEY_LABEL = "label"
    }
}