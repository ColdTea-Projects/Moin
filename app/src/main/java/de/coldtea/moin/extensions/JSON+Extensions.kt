package de.coldtea.moin.extensions

import com.squareup.moshi.Moshi
import de.coldtea.moin.services.model.AlarmList
import de.coldtea.moin.services.model.InfoPairs

fun String.convertToAlarmList(): AlarmList? =
    Moshi
        .Builder()
        .build()
        .adapter(AlarmList::class.java)
        .fromJson(this)

fun String.convertToInfoPairs(): InfoPairs? =
    Moshi
        .Builder()
        .build()
        .adapter(InfoPairs::class.java)
        .fromJson(this)
