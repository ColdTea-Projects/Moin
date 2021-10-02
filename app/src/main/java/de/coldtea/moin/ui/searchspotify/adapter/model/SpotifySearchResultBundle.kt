package de.coldtea.moin.ui.searchspotify.adapter.model

data class SpotifySearchResultBundle(
    val id: String,
    val spotifySearchResultDelegateItem: SpotifySearchResultDelegateItem,
    val onClickPlay: (id: String) -> Unit,
    val onClickItem: (id: String) -> Unit,
    var playState: Boolean = false
)