package de.coldtea.moin.ui.diffutils

import androidx.recyclerview.widget.DiffUtil
import de.coldtea.moin.ui.searchspotify.adapter.model.SpotifySearchResultBundle

class SpotifySearchResultDiffUtilCallback : DiffUtil.ItemCallback<SpotifySearchResultBundle>() {

    override fun areItemsTheSame(oldItem: SpotifySearchResultBundle, newItem: SpotifySearchResultBundle): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SpotifySearchResultBundle, newItem: SpotifySearchResultBundle): Boolean =
        oldItem.hashCode() == newItem.hashCode()

}