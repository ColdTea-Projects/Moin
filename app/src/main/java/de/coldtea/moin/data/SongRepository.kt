package de.coldtea.moin.data

import de.coldtea.moin.data.database.MoinDatabase
import de.coldtea.moin.data.database.entity.SongEntity
import de.coldtea.moin.domain.model.extensions.toSong
import de.coldtea.moin.domain.model.playlist.PlaylistName
import de.coldtea.moin.domain.model.playlist.Song

class SongRepository(
    moinDatabase: MoinDatabase
) {
    private val daoSong = moinDatabase.daoSong

    suspend fun addSong(song: Song) =
        daoSong.insert(
            SongEntity(
                trackId = song.trackId,
                name = song.name,
                artistName = song.artistName,
                imageUrl = song.imageUrl,
                mediaType = song.mediaType,
                source = song.source,
                playlist = song.playlist
            )
        )

    suspend fun deleteSong(id: Int) =
        daoSong.deleteSong(id)

    suspend fun getSongsByPlaylist(playlist: PlaylistName) =
        daoSong.getSongs(playlist.ordinal).map { it.toSong() }

    suspend fun getSongs() =
        daoSong.getSongs().map { it.toSong() }

}