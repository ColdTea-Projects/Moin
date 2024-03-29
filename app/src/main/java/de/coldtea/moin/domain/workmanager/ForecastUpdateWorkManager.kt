package de.coldtea.moin.domain.workmanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object ForecastUpdateWorkManager {
    private const val FORECAST_UPDATE_WORK = "forecast_update_work"

    private val workRequestConstraints by lazy {
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    }

    private val periodicUpdateRequest =
        PeriodicWorkRequestBuilder<ForecastUpdateWork>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(workRequestConstraints)
            .build()

    fun startPeriodicalForecastUpdate(context: Context) = WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(
            FORECAST_UPDATE_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicUpdateRequest
        )
}