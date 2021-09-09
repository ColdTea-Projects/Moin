package de.coldtea.moin.di

import de.coldtea.moin.services.GeolocationService
import de.coldtea.moin.services.SmplrAlarmService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val servicesModule = module {
    single { SmplrAlarmService(androidContext()) }
    single{ GeolocationService(androidContext()) }
}