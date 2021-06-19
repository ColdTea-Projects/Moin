package de.coldtea.moin.extensions

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import de.coldtea.moin.R
import de.coldtea.moin.databinding.ViewAlarmDelegateItemBinding
import de.coldtea.smplr.smplralarm.models.WeekDays

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