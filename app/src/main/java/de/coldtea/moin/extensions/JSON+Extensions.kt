package de.coldtea.moin.extensions

import de.coldtea.moin.di.json
import de.coldtea.moin.domain.model.alarm.AlarmList
import de.coldtea.moin.domain.model.alarm.InfoPairs
import kotlinx.serialization.decodeFromString

fun String.convertToAlarmList(): AlarmList =
    json.decodeFromString(this)

fun String.convertToInfoPairs(): InfoPairs =
    json.decodeFromString(this)