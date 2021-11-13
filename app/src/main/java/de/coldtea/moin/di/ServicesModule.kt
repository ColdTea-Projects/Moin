package de.coldtea.moin.di

import de.coldtea.moin.domain.services.GeolocationService
import de.coldtea.moin.domain.services.RingerService
import de.coldtea.moin.domain.services.SmplrAlarmService
import de.coldtea.moin.domain.services.SongRandomizeService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val servicesModule = module {
    single { SmplrAlarmService(androidContext().applicationContext) }
    single { GeolocationService(androidContext().applicationContext, get()) }
    single { RingerService(androidContext().applicationContext, get(), get()) }
    single { SongRandomizeService(get(), get(), get(), get()) }
}