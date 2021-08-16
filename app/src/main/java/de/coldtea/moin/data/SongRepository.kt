package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.database.entity.SongEntity
import de.coldtea.moin.model.song.Playlist
import de.coldtea.moin.model.song.Song

class SongRepository(
    moinDatabase: MoinDatabase
) {
    private val daoSong = moinDatabase.daoSong

    suspend fun addSong(song: Song) =
        daoSong.insert(
            SongEntity(
                name = song.name,
                albumName = song.albumName,
                imageUrl = song.imageUrl,
                mediaType = song.mediaType,
                source = song.source,
                playlist = song.playlist
            )
        )

    suspend fun deleteSong(id: Int) =
        daoSong.deleteSong(id)

    suspend fun getSongsByPlaylist(playlist: Playlist) =
        daoSong.getSongs(playlist.ordinal)

    suspend fun getSongs() =
        daoSong.getSongs()

}