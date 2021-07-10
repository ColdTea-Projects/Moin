package de.coldtea.moin.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_forecast")
data class HourlyForecastEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "hourly_forecast_id")
    val hourlyForecastId: Int = 0,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "temp_c")
    val tempC: Double,
    @ColumnInfo(name = "temp_f")
    val tempF: Double,
    @ColumnInfo(name = "condition_code")
    val conditionCode: Int,
    @ColumnInfo(name = "condition_text")
    val conditionText: String
)