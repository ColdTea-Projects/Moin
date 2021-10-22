package de.coldtea.moin.extensions

import de.coldtea.moin.data.network.spotify.model.AccessTokenRequestParametersResponse
import de.coldtea.moin.di.json
import de.coldtea.moin.domain.model.alarm.AlarmList
import de.coldtea.moin.domain.model.alarm.InfoPairs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@ExperimentalSerializationApi
fun String.convertToAlarmList(): AlarmList =
    json.decodeFromString(this)

@ExperimentalSerializationApi
fun String.convertToInfoPairs(): InfoPairs =
    json.decodeFromString(this)

@ExperimentalSerializationApi
fun AccessTokenRequestParametersResponse.convertToString(): String =
    json.encodeToString(this)
