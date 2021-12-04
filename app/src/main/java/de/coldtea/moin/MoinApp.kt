package de.coldtea.moin

import android.app.Application
import de.coldtea.moin.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class MoinApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        // Start Koin
        startKoin{
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MoinApp)

            modules(
                dataModule,
                servicesModule,
                networkModule,
                repositoryModule,
                appModule
            )
        }
    }
}