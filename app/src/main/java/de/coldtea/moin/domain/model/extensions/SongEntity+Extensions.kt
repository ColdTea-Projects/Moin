package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.data.database.entity.SongEntity
import de.coldtea.moin.domain.model.playlist.Song

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