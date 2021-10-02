package de.coldtea.moin.domain.model.extensions

import de.coldtea.moin.domain.model.spotify.SearchResult
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultBundle
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem

fun SearchResult.getSearchResultBundle(
    onClickPlay: (id: String) -> Unit,
    onClickItem: (id: String) -> Unit
) =
    this.tracks.items.map {
        SpotifySearchResultBundle(
            id = it.id,
            spotifySearchResultDelegateItem = SpotifySearchResultDelegateItem(
                imageUrl = it.album.images[0]?.url,
                songName = it.name,
                artistName = it.artists.joinToString(", ") { artist -> artist.name }
            ),
            onClickPlay = onClickPlay,
            onClickItem = onClickItem
        )

    }