package de.coldtea.moin.extensions

import de.coldtea.smplr.smplralarm.models.WeekDays
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

//TODO: Write tests
fun Pair<Int, Int>.getTimeText(): String {
    val time = Calendar.getInstance().apply {
        set(HOUR_OF_DAY, first)
        set(MINUTE, second)
    }

    val dateFormat = SimpleDateFormat("HH:mm")
    return dateFormat.format(time.time)
}

fun List<WeekDays>.getWeekDaysText(): String {
    var weekdays: String = ""
    if (WeekDays.MONDAY in this) weekdays += "MON "
    if (WeekDays.TUESDAY in this) weekdays += "TUE "
    if (WeekDays.WEDNESDAY in this) weekdays += "WED "
    if (WeekDays.THURSDAY in this) weekdays += "TH "
    if (WeekDays.FRIDAY in this) weekdays += "FR "
    if (WeekDays.SATURDAY in this) weekdays += "SAT "
    if (WeekDays.SUNDAY in this) weekdays += "SUN "

    return weekdays.trim()
}