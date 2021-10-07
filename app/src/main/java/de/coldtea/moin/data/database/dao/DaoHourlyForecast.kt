package de.coldtea.moin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.coldtea.moin.data.database.entity.HourlyForecastEntity

@Dao
abstract class DaoHourlyForecast : DaoBase<HourlyForecastEntity> {

    @Transaction
    @Query("SELECT * FROM hourly_forecast")
    abstract suspend fun getHourlyForecasts() : List<HourlyForecastEntity>

    @Transaction
    @Query("SELECT * FROM hourly_forecast WHERE time_epoch = :nowEpoch")
    abstract suspend fun getForecastsAt(nowEpoch: Int) : HourlyForecastEntity?

    @Transaction
    @Query("DELETE FROM hourly_forecast WHERE time_epoch < :nowEpoch")
    abstract suspend fun removeOutdatedForecasts(nowEpoch: Int)

}