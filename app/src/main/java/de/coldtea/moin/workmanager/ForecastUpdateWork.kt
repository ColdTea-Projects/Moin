package de.coldtea.moin.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.services.GeolocationService
import org.koin.java.KoinJavaComponent.inject
import retrofit2.HttpException
import timber.log.Timber

class ForecastUpdateWork(context: Context, workerParameters: WorkerParameters): CoroutineWorker(context, workerParameters) {
    private val weatherRepository: WeatherRepository by inject(WeatherRepository::class.java)
    private val geolocationService: GeolocationService by inject(GeolocationService::class.java)

    override suspend fun doWork(): Result {
        val city = geolocationService.getCityName() ?: return Result.failure()

        Timber.i("Moin geolocationService --> $geolocationService")
        Timber.i("Moin city --> $city")
        try {
            weatherRepository.updateWeatherForecast(city)
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
}