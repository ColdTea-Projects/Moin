package de.coldtea.moin.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_local_id")
    val songLocalId: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "album_name")
    val albumName: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "media_type")
    val mediaType: Int,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "playlist")
    val playlist: Int
)