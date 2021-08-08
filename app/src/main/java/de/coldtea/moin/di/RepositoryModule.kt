package de.coldtea.moin.di

import de.coldtea.moin.data.SpotifyAuthRepository
import de.coldtea.moin.data.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { WeatherRepository(get(), get()) }
    factory { SpotifyAuthRepository(get(), get(), get()) }
}