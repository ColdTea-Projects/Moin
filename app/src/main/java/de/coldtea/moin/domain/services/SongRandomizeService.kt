package de.coldtea.moin.domain.services

import de.coldtea.moin.data.SharedPreferencesRepository
import de.coldtea.moin.data.SongRepository
import de.coldtea.moin.data.WeatherRepository
import de.coldtea.moin.domain.model.extensions.toRingerScreenInfo
import de.coldtea.moin.domain.model.forecast.*
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.Song
import de.coldtea.moin.domain.model.ringer.RingerScreenInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import timber.log.Timber

class SongRandomizeService(
    private val weatherRepository: WeatherRepository,
    private val geolocationService: GeolocationService,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val songRepository: SongRepository
) {

    suspend fun getRingerScreenInfo(): RingerScreenInfo? = withContext(Dispatchers.IO) {
        val city = geolocationService.getCityName()

        withTimeout(3000L) {
            if (!city.isNullOrEmpty() && city != sharedPreferencesRepository.lastVisitedCity) {
                try {
                    Timber.i("Moin --> updateWeatherForecast - SongRandomizeService")
                    weatherRepository.updateWeatherForecast(city).also {
                        sharedPreferencesRepository.lastVisitedCity = city
                    }
                }catch (e: HttpException){
                    Timber.e("Moin --> $e")
                }catch (e: Exception){
                    Timber.e("Moin --> $e")
                }
            }
        }

        val forecast = weatherRepository.getForecast()

        return@withContext forecast?.toRingerScreenInfo(
            getRandomSong(forecast)
        )
    }

    private suspend fun getRandomSong(forecast: HourlyForecast?): Song? {
        val playlist = if (forecast != null) {
            findMainForecastCondition(forecast.conditionCode)
        } else null

        return getRandomSongByPlaylist(playlist)
    }

    private suspend fun getRandomSongByPlaylist(playlistName: PlaylistName?): Song? {
        if (playlistName != null) {
            val songs = songRepository.getSongsByPlaylist(playlistName)
            if (songs.isEmpty())
                return getRandomSongByPlaylist(null)

            return songs.shuffled().take(1)[0]
        } else {
            val songs = songRepository.getSongs()
            if(songs.isEmpty()){
                return null
            }
            return songs.shuffled().take(1)[0]
        }
    }

    private fun findMainForecastCondition(code: Int): PlaylistName? = when {
        SUNNY_WEATHER_BUNDLE.conditionCodes.contains(code) -> SUNNY_WEATHER_BUNDLE.playlistName
        CLOUDY_WEATHER_BUNDLE.conditionCodes.contains(code) -> CLOUDY_WEATHER_BUNDLE.playlistName
        RAINY_WEATHER_BUNDLE.conditionCodes.contains(code) -> RAINY_WEATHER_BUNDLE.playlistName
        SNOWY_WEATHER_BUNDLE.conditionCodes.contains(code) -> SNOWY_WEATHER_BUNDLE.playlistName
        else -> null
    }

}