package de.coldtea.moin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.coldtea.moin.data.database.entity.SongEntity

@Dao
abstract class DaoSong : DaoBase<SongEntity> {

    @Transaction
    @Query("SELECT * FROM song")
    abstract suspend fun getSongs() : List<SongEntity>

    @Transaction
    @Query("SELECT * FROM song WHERE playlist = :playlist")
    abstract suspend fun getSongs(playlist: Int) : List<SongEntity>

    @Transaction
    @Query("DELETE FROM song WHERE song_local_id = :songLocalId")
    abstract suspend fun deleteSong(songLocalId: Int)

    @Transaction
    @Query("DELETE FROM song WHERE song_local_id IN (:songLocalIds)")
    abstract suspend fun deleteMultipleSongs(songLocalIds: List<Int>)

}