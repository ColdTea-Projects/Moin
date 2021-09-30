package de.coldtea.moin.ui.diffutils

import androidx.recyclerview.widget.DiffUtil
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultDelegateItem

class SpotifySearchResultDiffUtilCallback : DiffUtil.ItemCallback<SpotifySearchResultDelegateItem>() {

    override fun areItemsTheSame(oldItem: SpotifySearchResultDelegateItem, newItem: SpotifySearchResultDelegateItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SpotifySearchResultDelegateItem, newItem: SpotifySearchResultDelegateItem): Boolean =
        oldItem.hashCode() == newItem.hashCode()

}