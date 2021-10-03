package de.coldtea.moin.ui.playlist.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import de.coldtea.moin.ui.diffutils.PlaylistDiffUtilCallback
import de.coldtea.moin.ui.playlist.adapter.delegates.PlaylistDelegate
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistBundle

class PlaylistAdapter: AsyncListDifferDelegationAdapter<PlaylistBundle>(
    PlaylistDiffUtilCallback()
) {
    init {
        delegatesManager.addDelegate(PlaylistDelegate())
    }
}