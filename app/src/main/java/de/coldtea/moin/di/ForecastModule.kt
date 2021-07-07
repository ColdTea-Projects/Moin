package de.coldtea.moin.di

import de.coldtea.moin.data.WeatherRepository
import org.koin.dsl.module

val forecastModule = module {
    factory { WeatherRepository(get()) }
}