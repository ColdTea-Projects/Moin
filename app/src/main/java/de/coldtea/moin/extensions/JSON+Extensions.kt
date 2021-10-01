package de.coldtea.moin.extensions

import com.squareup.moshi.Moshi
import de.coldtea.moin.data.network.spotify.model.AccessTokenRequestParametersResponse
import de.coldtea.moin.domain.model.alarm.AlarmList
import de.coldtea.moin.domain.model.alarm.InfoPairs

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

fun AccessTokenRequestParametersResponse.convertToString(): String? =
    Moshi
        .Builder()
        .build()
        .adapter(AccessTokenRequestParametersResponse::class.java)
        .toJson(this)
