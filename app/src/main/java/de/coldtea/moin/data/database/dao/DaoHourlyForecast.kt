package de.coldtea.moin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.coldtea.moin.data.database.entity.HourlyForecastEntity

@Dao
abstract class DaoHourlyForecast : DaoBase<HourlyForecastEntity> {

    @Transaction
    @Query("SELECT * From hourly_forecast")
    abstract suspend fun getHourlyForecasts() : List<HourlyForecastEntity>

}