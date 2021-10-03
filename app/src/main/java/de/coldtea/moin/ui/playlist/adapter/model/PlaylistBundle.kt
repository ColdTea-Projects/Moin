package de.coldtea.moin.ui.playlist.adapter.model

data class PlaylistBundle(
    val id: Int,
    val playlistDelegateItem: PlaylistDelegateItem,
    val onClickDelete: (id: Int) -> Unit
)