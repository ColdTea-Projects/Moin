package de.coldtea.moin.extensions

import de.coldtea.smplr.smplralarm.models.WeekDays
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.util.Calendar.*

//TODO: Write tests
fun Pair<Int, Int>.getTimeText(): String {
    val time = Calendar.getInstance().apply {
        set(HOUR_OF_DAY, first)
        set(MINUTE, second)
    }

    val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return dateFormat.format(time.time)
}

fun List<WeekDays>.getWeekDaysText(): String {
    var weekdays = ""
    if (WeekDays.MONDAY in this) weekdays += "MON "
    if (WeekDays.TUESDAY in this) weekdays += "TUE "
    if (WeekDays.WEDNESDAY in this) weekdays += "WED "
    if (WeekDays.THURSDAY in this) weekdays += "TH "
    if (WeekDays.FRIDAY in this) weekdays += "FR "
    if (WeekDays.SATURDAY in this) weekdays += "SAT "
    if (WeekDays.SUNDAY in this) weekdays += "SUN "

    return weekdays.trim()
}

fun getTopOfTheHour() = Calendar.getInstance().apply {
    set(MILLISECOND, 0)
    set(SECOND, 0)
    set(MINUTE, 0)
}.timeInSeconds()

fun Calendar.isDayTime(): Boolean = get(HOUR_OF_DAY) in 6..18

fun Calendar.timeInSeconds(): Int =
    (this.timeInMillis / 1000).toInt()

fun Calendar.getTimeExactForAlarm(
    hour: Int,
    minute: Int,
    weekDays: List<WeekDays>
): Calendar {
    timeInMillis = System.currentTimeMillis()

    set(HOUR_OF_DAY, hour)
    set(MINUTE, minute)
    set(SECOND, 0)
    set(MILLISECOND, 0)

    val sortedWeekDays = weekDays.sortedBy { it.value }

    when{
        weekDays.isNotEmpty() && !isAlarmForToday(sortedWeekDays, hour, minute) -> setTheDay(sortedWeekDays.getClosestDay())
        weekDays.isEmpty() && !isTimeAhead(hour, minute) -> add(DATE, 1)
    }

    return this
}

private fun Calendar.setTheDay(nextWeekDay: Int) {
    val todayDayOfWeek = get(DAY_OF_WEEK)

    if (todayDayOfWeek < nextWeekDay) {
        set(DAY_OF_WEEK, nextWeekDay)
        return
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val temporalDayOfWeek = when (nextWeekDay) {
            1 -> DayOfWeek.SUNDAY
            2 -> DayOfWeek.MONDAY
            3 -> DayOfWeek.TUESDAY
            4 -> DayOfWeek.WEDNESDAY
            5 -> DayOfWeek.THURSDAY
            6 -> DayOfWeek.FRIDAY
            7 -> DayOfWeek.SATURDAY
            else -> null
        }

        if (temporalDayOfWeek == null) {
            Timber.e("SmplrAlarm -> The day of week could not be set!")
            return
        }

        val localDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            .with(TemporalAdjusters.next(temporalDayOfWeek))
        set(DAY_OF_YEAR, localDate.dayOfYear)
    } else {
        val date = get(DAY_OF_MONTH)
        val day = get(DAY_OF_WEEK)

        val daysToPostpone = if ((nextWeekDay + 7 - day) % 7 == 0) 7 else (nextWeekDay + 7 - day) % 7
        set(DAY_OF_MONTH, date + daysToPostpone)
    }
}

private fun isTimeAhead(hour: Int, minute: Int) = Calendar.getInstance().let {
    it.get(HOUR_OF_DAY) < hour
            || (it.get(HOUR_OF_DAY) == hour &&
            it.get(MINUTE) < minute)
}

private fun List<WeekDays>.getClosestDay(): Int =
    this.map { it.value }
        .firstOrNull {
            val today = getInstance().get(DAY_OF_WEEK)
            it > today
        }
        ?: this.first().value

private fun isAlarmForToday(weekDays: List<WeekDays>, hour: Int, minute: Int): Boolean = getInstance().let {
    weekDays.map { weekDay -> weekDay.value }.contains(it.get(DAY_OF_WEEK)) && isTimeAhead(hour, minute)
}
