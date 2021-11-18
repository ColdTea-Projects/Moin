package de.coldtea.moin.extensions

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ViewAlarmDelegateItemBinding
import de.coldtea.moin.ui.alarm.adapter.model.AlarmDelegateItem
import de.coldtea.moin.ui.diffutils.AlarmsDiffUtilCallback
import de.coldtea.smplr.smplralarm.models.WeekDays
import kotlinx.android.synthetic.main.view_alarm_delegate_item.view.*

val ViewAlarmDelegateItemBinding.weekdaysList: List<WeekDays>
    get() {
        val weekDays = mutableListOf<WeekDays>()

        if (monday.isChecked) weekDays.add(WeekDays.MONDAY)
        if (tuesday.isChecked) weekDays.add(WeekDays.TUESDAY)
        if (wednesday.isChecked) weekDays.add(WeekDays.WEDNESDAY)
        if (thursday.isChecked) weekDays.add(WeekDays.THURSDAY)
        if (friday.isChecked) weekDays.add(WeekDays.FRIDAY)
        if (saturday.isChecked) weekDays.add(WeekDays.SATURDAY)
        if (sunday.isChecked) weekDays.add(WeekDays.SUNDAY)

        return weekDays.toList()
    }

fun ViewAlarmDelegateItemBinding.drawAlarmListItem(item: AlarmDelegateItem){
    val hourMinute = item.originalHour to item.originalMinute

    time.text = hourMinute.getTimeText()
    days.text = item.weekDays.getWeekDaysText()
    days.isVisible = !item.isExpanded

    isActive.isChecked = item.isActive

    snooze.text = root.context?.getString(R.string.snooze_until, (item.hour to item.minute).getTimeText())
    snooze.isVisible = item.hour != item.originalHour || item.minute != item.originalMinute

    label.label.text = item.label

    setupCheckList(item.weekDays)

    repeat.isChecked = item.weekDays.isNotEmpty()

    expand.scaleY = if(item.isExpanded) -1F else 1F

    groupHidden.isVisible = item.isExpanded
    groupWeekdays.isVisible = item.isExpanded
}

fun ViewAlarmDelegateItemBinding.upgradeByPayload(payloads: Any?){
    if (payloads is Map<*, *>){
        payloads.map {  payload ->
            when(payload.key){
                AlarmsDiffUtilCallback.KEY_TIME -> {
                    time.text = payload.value as String
                    cleanSnoozeView()
                }
                AlarmsDiffUtilCallback.KEY_SNOOZETIME -> {
                    val snoozeTime = payload.value as String
                    snooze.text = root.context?.getString(R.string.snooze_until, snoozeTime)
                    snooze.isVisible = true
                }
                AlarmsDiffUtilCallback.KEY_WEEKDAYS -> {}
                AlarmsDiffUtilCallback.KEY_ISACTIVE -> {
                    isActive.isChecked = payload.value as Boolean
                }
                AlarmsDiffUtilCallback.KEY_ISEXPANDED -> {
                    val expanded = payload.value as Boolean
                    days.isVisible = !expanded

                    expand.scaleY = if(expanded) -1F else 1F
                    groupHidden.isVisible = expanded
                    groupWeekdays.isVisible = expanded
                }
                AlarmsDiffUtilCallback.KEY_LABEL -> {
                    label.label.text = payload.value as String
                }
            }
        }


    }
}

fun ViewAlarmDelegateItemBinding.unifyChecklist(isChecked: Boolean) {
    monday.isChecked = isChecked
    tuesday.isChecked = isChecked
    wednesday.isChecked = isChecked
    thursday.isChecked = isChecked
    friday.isChecked = isChecked
    saturday.isChecked = isChecked
    sunday.isChecked = isChecked
}

fun ViewAlarmDelegateItemBinding.setupCheckList(weekDays: List<WeekDays>) {
    monday.isChecked = weekDays.contains(WeekDays.MONDAY)
    tuesday.isChecked = weekDays.contains(WeekDays.TUESDAY)
    wednesday.isChecked = weekDays.contains(WeekDays.WEDNESDAY)
    thursday.isChecked = weekDays.contains(WeekDays.THURSDAY)
    friday.isChecked = weekDays.contains(WeekDays.FRIDAY)
    saturday.isChecked = weekDays.contains(WeekDays.SATURDAY)
    sunday.isChecked = weekDays.contains(WeekDays.SUNDAY)
}

fun ViewAlarmDelegateItemBinding.activateAlarmUI(){
    groupHidden.isVisible = false
    groupWeekdays.isVisible = false
    days.isVisible = true

    expand.scaleY = 1F

    val itemColor = R.color.moinBlueTone7
    time.setTextColor(ContextCompat.getColor(this.time.context, itemColor))
    days.setTextColor(ContextCompat.getColor(this.time.context, itemColor))
    expand.setColorFilter(ContextCompat.getColor(this.time.context, itemColor))

}

fun ViewAlarmDelegateItemBinding.deactivateAlarmUI(){
    groupHidden.isVisible = false
    groupWeekdays.isVisible = false
    days.isVisible = true

    expand.scaleY = 1F

    val itemColor = R.color.moinGray3
    time.setTextColor(ContextCompat.getColor(this.time.context, itemColor))
    days.setTextColor(ContextCompat.getColor(this.time.context, itemColor))
    expand.setColorFilter(ContextCompat.getColor(this.time.context, itemColor))

}

fun ViewAlarmDelegateItemBinding.cleanSnoozeView(){
    snooze.text = ""
    snooze.isVisible = false
}