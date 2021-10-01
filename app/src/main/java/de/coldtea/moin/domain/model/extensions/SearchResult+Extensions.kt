package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.domain.model.spotify.SearchResult
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem

fun SearchResult.getSongList() =
    this.tracks.items.map {
        SpotifySearchResultDelegateItem(
            id = it.id,
            imageUrl = it.album.images[0]?.url,
            songName = it.name,
            artistName = it.artists.joinToString(", ") { artist -> artist.name }
        )
    }