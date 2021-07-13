package de.coldtea.moin.di

import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.database.MoinDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module{
    single { MoinDatabase.getInstance(androidApplication().applicationContext) }
    single { SharedPreferencesRepository(androidApplication().applicationContext) }
}