package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.data.database.entity.SongEntity
import de.coldtea.moin.domain.model.playlist.Song
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistBundle
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistDelegateItem

fun SongEntity.toSong() =
    Song(
       localId = songLocalId,
       trackId = trackId,
       name = name,
       artistName = artistName,
       imageUrl = imageUrl,
       mediaType = mediaType,
       source = source,
       playlist = playlist
    )

fun Song.getPlaylistBundle(onClickDelete: (id: Int) -> Unit) =
    PlaylistBundle(
        id = localId,
        playlistDelegateItem = PlaylistDelegateItem(
            imageUrl = imageUrl,
            songName = name,
            artistName = artistName
        ),
        onClickDelete = onClickDelete
    )