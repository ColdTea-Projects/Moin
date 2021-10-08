package de.coldtea.moin.domain.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.domain.model.extensions.toForecastBoundary
import de.coldtea.moin.domain.services.GeolocationService
import org.koin.java.KoinJavaComponent.inject
import retrofit2.HttpException
import timber.log.Timber

class ForecastUpdateWork(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    private val weatherRepository: WeatherRepository by inject(WeatherRepository::class.java)
    private val geolocationService: GeolocationService by inject(GeolocationService::class.java)
    private val sharedPreferencesRepository: SharedPreferencesRepository by inject(
        SharedPreferencesRepository::class.java
    )

    override suspend fun doWork(): Result {
        val city = geolocationService.getCityName()
            ?: sharedPreferencesRepository.lastVisitedCity
            ?: return Result.failure()

        Timber.i("Moin geolocationService --> $geolocationService")
        Timber.i("Moin city --> $city")
        try {
            if (isUpdateNeeded(city)) weatherRepository.updateWeatherForecast(city)
            Timber.i("Moin-getWeatherForecast- the weather forecast is updated")
        } catch (ex: HttpException) {
            Timber.i("Moin-getWeatherForecast- HTTP Request Error: $ex")
            return Result.failure()
        } catch (ex: Exception) {
            Timber.i("Moin-getWeatherForecast- Error: $ex")
            return Result.retry()
        }

        return Result.success()
    }

    private suspend fun isUpdateNeeded(cityName: String): Boolean {
        if (sharedPreferencesRepository.lastBulkForecastFetch == -1) return true

        val forecastBoundaryObject = weatherRepository
            .getHourlyForecastByCity(cityName)
            .toForecastBoundary()

        if (forecastBoundaryObject.noForecastData()
            || forecastBoundaryObject.dataSizeNotBigEnough()
            || forecastBoundaryObject.isOutdated()
        ) return true

        return false
    }
}