package de.coldtea.moin.data.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.coldtea.moin.data.database.dao.DaoHourlyForecast
import de.coldtea.moin.data.database.dao.DaoSong
import de.coldtea.moin.data.database.entity.HourlyForecastEntity
import de.coldtea.moin.data.database.entity.SongEntity

@SuppressLint("RestrictedApi")
@Database(
    entities = [
        HourlyForecastEntity::class,
        SongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoinDatabase : RoomDatabase() {

    abstract val daoHourlyForecast: DaoHourlyForecast
    abstract val daoSong: DaoSong

    //TODO: try if this works without locally ensured singleton
    companion object {
        @Volatile
        private var INSTANCE: MoinDatabase? = null
        internal fun getInstance(context: Context): MoinDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MoinDatabase::class.java,
                        "db_moin_app"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}