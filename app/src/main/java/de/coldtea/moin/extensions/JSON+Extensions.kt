package de.coldtea.moin.extensions

import com.squareup.moshi.Moshi
import de.coldtea.moin.services.model.AlarmList

fun String.convertToAlarmList(): AlarmList? =
    Moshi
        .Builder()
        .build()
        .adapter(AlarmList::class.java)
        .fromJson(this)