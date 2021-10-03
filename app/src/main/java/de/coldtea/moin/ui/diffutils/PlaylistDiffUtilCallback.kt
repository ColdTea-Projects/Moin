package de.coldtea.moin.ui.diffutils

import androidx.recyclerview.widget.DiffUtil
import de.coldtea.moin.ui.playlist.adapter.model.PlaylistBundle

class PlaylistDiffUtilCallback : DiffUtil.ItemCallback<PlaylistBundle>() {

    override fun areItemsTheSame(oldItem: PlaylistBundle, newItem: PlaylistBundle): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PlaylistBundle, newItem: PlaylistBundle): Boolean =
        oldItem.hashCode() == newItem.hashCode()

}