package de.coldtea.moin.ui.searchspotify.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import de.coldtea.moin.ui.diffutils.SpotifySearchResultDiffUtilCallback
import de.coldtea.moin.ui.searchspotify.adapter.delegates.SpotifySearchResultDelegate
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem

class SpotifySearchAdapter: AsyncListDifferDelegationAdapter<SpotifySearchResultDelegateItem>(
    SpotifySearchResultDiffUtilCallback()
) {
    init {
        delegatesManager.addDelegate(SpotifySearchResultDelegate())
    }
}