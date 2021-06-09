package de.coldtea.moin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.coldtea.moin.services.SmplrAlarmService
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val smplrAlarmService: SmplrAlarmService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moin_activity_main)
        val notificationItem = NotificationItem(
            R.drawable.ic_baseline_access_alarm_24,
            "Welcome to MoinApp",
            "Welcome to MoinApp",
            "Welcome to MoinApp",
            true,
            null, null, null, null
        )

        smplrAlarmService.setAlarm(
            hour = 22, minute = 33, notificationItem = notificationItem, weekDays = listOf(WeekDays.WEDNESDAY)
        )
    }

}